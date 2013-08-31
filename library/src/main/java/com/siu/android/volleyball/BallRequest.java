package com.siu.android.volleyball;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.siu.android.volleyball.local.LocalRequestProcessor;

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
    protected Response.Listener<T> mListener;
    protected BallResponse.ListenerWithLocalProcessing<T> mListenerWithLocalProcessing;

    protected boolean networkResponseDelivered = false;
    protected boolean intermediateResponseDelivered = false;
    protected boolean finished = false;


    protected BallRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }

    protected BallRequest(int method, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    protected BallRequest(int method, String url, BallResponse.ListenerWithLocalProcessing<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListenerWithLocalProcessing = listener;
    }

    @Override
    protected void deliverResponse(T response) {
        if (shouldProcessLocal()) {
            mListenerWithLocalProcessing.onRemoteResponse(response);
        } else {
            mListener.onResponse(response);
        }
    }

    /* Override from parent because of return type or proctected scope */

    @Override
    protected final Response<T> parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    abstract protected BallResponse<T> parseBallNetworkResponse(NetworkResponse response);


    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return super.parseNetworkError(volleyError);
    }

    public void addMarker(String tag) {
        if (BallMarkerLog.ENABLED) {
            mEventLog.add(tag, Thread.currentThread().getId());
        } else if (mRequestBirthTime == 0) {
            mRequestBirthTime = SystemClock.elapsedRealtime();
        }
    }

    public void finish(final String tag) {
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


    /* Override to get local request support */

    public boolean shouldProcessLocal() {
        return false;
    }

    protected LocalRequestProcessor<T> createLocalRequestProcessor() {
        return null;
    }



    /* Gets and sets */

    public LocalRequestProcessor<T> getLocalRequestProcessor() {
        return mLocalRequestProcessor;
    }

    public boolean isNetworkResponseDelivered() {
        return networkResponseDelivered;
    }

    public void setNetworkResponseDelivered(boolean networkResponseDelivered) {
        this.networkResponseDelivered = networkResponseDelivered;
    }

    public boolean isIntermediateResponseDelivered() {
        return intermediateResponseDelivered;
    }

    public void setIntermediateResponseDelivered(boolean intermediateResponseDelivered) {
        this.intermediateResponseDelivered = intermediateResponseDelivered;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}