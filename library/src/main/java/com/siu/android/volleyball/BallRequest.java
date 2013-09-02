package com.siu.android.volleyball;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.siu.android.volleyball.exception.BallException;
import com.siu.android.volleyball.local.LocalRequestProcessor;
import com.siu.android.volleyball.network.NetworkRequestProcessor;
import com.siu.android.volleyball.response.ResponseListener;

/**
 * Created by lukas on 8/29/13.
 */
public abstract class BallRequest<T> extends Request<T> {

    /* Private fields from Request class */
    protected static final long SLOW_REQUEST_THRESHOLD_MS = 3000;
    protected final BallMarkerLog mEventLog = BallMarkerLog.ENABLED ? new BallMarkerLog() : null;
    protected long mRequestBirthTime = 0;
    protected BallRequestQueue mRequestQueue;


    /* Additionnal logic from Ball */
    protected LocalRequestProcessor<T> mLocalRequestProcessor;
    protected NetworkRequestProcessor<T> mNetworkRequestProcessor;
    protected ResponseListener mResponseListener;

    /**
     * Error from final response, stored and used later if intermediate response is still to be delivered:
     * If intermediate response is delivered after with no response, deliver the final error
     */
    private VolleyError mFinalResponseError;

    protected boolean mFinalResponseDelivered = false;

    /**
     * Intermediate response of the request has been delivered.
     * <p/>
     * Several use cases:
     * - In the executor delivery to consider only the 1st intermediate request and ignore the 2nd one.
     * - REMOVED FOR NOW, NOT VOLATILE -- In the network dispatcher to return identical response in case of 304 not modified response
     * and if a valid intermediate response was returned. Must be volatile because it can apply between
     * local and network thread.
     */
    protected boolean mIntermediateResponseDelivered = false;

    /**
     * Request is finished and no more response should be delivered
     * Volatile because it is used to determine if a marker should be added to the log,
     * and value need to be synchronized between all worker threads
     */
    protected volatile boolean mFinished = false;


    protected BallRequest(int method, String url, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        if (shouldProcessLocal()) {
            mLocalRequestProcessor = createLocalRequestProcessor();

            if (mLocalRequestProcessor == null) {
                throw new BallException("Request should process local but local request processor is not provided");
            }
        }

        if (shouldProcessNetwork()) {
            mNetworkRequestProcessor = createNetworkRequestProcessor();

            if (mNetworkRequestProcessor == null) {
                throw new BallException("Request should process network but network request processor is not provided");
            }
        }

    }

    protected BallRequest(int method, String url, ResponseListener<T> responseListener, Response.ErrorListener errorListener) {
        this(method, url, errorListener);
        mResponseListener = responseListener;
    }

    @Override
    public void deliverResponse(T response) {
        throw new BallException("Illegal call to #deliverResponse(), you need to call the new #deliverIntermediate and #deliverFinal methods");
    }


    /* Override from parent because of return type or proctected scope */

    @Override
    protected final Response<T> parseNetworkResponse(NetworkResponse response) {
        throw new BallException("Illegal call to #parseBallNetworkResponse, you need to call the new #parseBallNetworkResponse() method");
    }

    //abstract protected BallResponse<T> parseBallNetworkResponse(NetworkResponse response);


    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return super.parseNetworkError(volleyError);
    }

    public void addMarker(String tag) {
        // ignore adding marker to finished log because it can happen when markers are added from several parallel threads
        if (mFinished) {
            return;
        }

        if (BallMarkerLog.ENABLED) {
            try {
                mEventLog.add(tag, Thread.currentThread().getId());
            } catch (IllegalStateException e) {
                // ignore exception from adding marker to finished log because it can happen when
                // markers are added from several parallel threads
            }
        } else if (mRequestBirthTime == 0) {
            mRequestBirthTime = SystemClock.elapsedRealtime();
        }
    }

    public void finish(final String tag) {
        if (mFinished) {
            throw new BallException("Trying to finish an already finished request");
        }

        mFinished = true;

        if (mRequestQueue != null) {
            mRequestQueue.finish(this);
        }

        if (BallMarkerLog.ENABLED) {
            final long threadId = Thread.currentThread().getId();
            if (Looper.myLooper() != Looper.getMainLooper()) {
                // If we finish marking off of the main thread, we need to
                // actually do it on the main thread to ensure correct ordering.
                Handler mainThread = new Handler(Looper.getMainLooper());
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mEventLog.add(tag, threadId);
                        mEventLog.finish(this.toString());
                    }
                });
                return;
            }

            mEventLog.add(tag, threadId);
            mEventLog.finish(this.toString());
        } else {
            long requestTime = SystemClock.elapsedRealtime() - mRequestBirthTime;
            if (requestTime >= SLOW_REQUEST_THRESHOLD_MS) {
                VolleyLog.d("%d ms: %s", requestTime, this.toString());
            }
        }
    }

    public void setRequestQueue(BallRequestQueue requestQueue) {
        mRequestQueue = requestQueue;
    }


    /* Override to get local processing */

    public boolean shouldProcessLocal() {
        return false;
    }

    protected LocalRequestProcessor<T> createLocalRequestProcessor() {
        return null;
    }


    /* Override to get network processing */

    public boolean shouldProcessNetwork() {
        return false;
    }

    protected NetworkRequestProcessor createNetworkRequestProcessor() {
        return null;
    }


    /* Complete request */

    public void deliverIntermediateResponse(T response, BallResponse.ResponseSource responseSource) {
        assertListenerExists();
        mResponseListener.onIntermediateResponse(response, responseSource);
    }

    public void deliverFinalResponse(T response, BallResponse.ResponseSource responseSource) {
        assertListenerExists();
        mResponseListener.onFinalResponse(response, responseSource);
    }

    public void deliverIdenticalFinalResponse(BallResponse.ResponseSource responseSource) {
        assertListenerExists();
        mResponseListener.onFinalResponseIdenticalToIntermediate(responseSource);
    }

    protected void assertListenerExists() {
        if (mResponseListener == null) {
            throw new BallException("Listener is null, you need to provide one or override deliverIntermediateResponse and deliverFinalResponse");
        }
    }

    public boolean isCompleteRequest() {
        return shouldProcessLocal() && shouldProcessNetwork();
    }



    /* Gets and sets */

    public LocalRequestProcessor<T> getLocalRequestProcessor() {
        return mLocalRequestProcessor;
    }

    public NetworkRequestProcessor<T> getNetworkRequestProcessor() {
        return mNetworkRequestProcessor;
    }

    public boolean isFinalResponseDelivered() {
        return mFinalResponseDelivered;
    }

    public void setFinalResponseDelivered(boolean finalResponseDelivered) {
        this.mFinalResponseDelivered = finalResponseDelivered;
    }

    public boolean isIntermediateResponseDelivered() {
        return mIntermediateResponseDelivered;
    }

    public void setIntermediateResponseDelivered(boolean intermediateResponseDelivered) {
        this.mIntermediateResponseDelivered = intermediateResponseDelivered;
    }

    public boolean isFinished() {
        return mFinished;
    }

    public void setFinished(boolean finished) {
        this.mFinished = finished;
    }

    public VolleyError getFinalResponseError() {
        return mFinalResponseError;
    }

    public void setFinalResponseError(VolleyError finalResponseError) {
        mFinalResponseError = finalResponseError;
    }


}