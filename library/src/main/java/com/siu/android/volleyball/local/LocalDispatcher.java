package com.siu.android.volleyball.local;

import android.os.Process;

import com.android.volley.VolleyLog;
import com.siu.android.volleyball.BallRequest;
import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.BallResponseDelivery;

import java.util.concurrent.BlockingQueue;

/**
 * Created by lukas on 8/26/13.
 */
public class LocalDispatcher extends Thread {

    private static final boolean DEBUG = VolleyLog.DEBUG;

    /**
     * The queue of requests coming in for triage.
     */
    private final BlockingQueue<BallRequest> mRequestQueue;

    private final BlockingQueue<BallRequest> mNetworkQueue;

    /**
     * For posting responses.
     */
    private final BallResponseDelivery mDelivery;

    /**
     * Used for telling us to die.
     */
    private volatile boolean mQuit = false;

    /**
     * Creates a new cache triage dispatcher thread.  You must call {@link #start()}
     * in order to begin processing.
     *
     * @param requestQueue Queue of incoming requests for triage
     * @param delivery     Delivery interface to use for posting responses
     */
    public LocalDispatcher(BlockingQueue<BallRequest> requestQueue, BlockingQueue<BallRequest> networkQueue, BallResponseDelivery delivery) {
        mRequestQueue = requestQueue;
        mNetworkQueue = networkQueue;
        mDelivery = delivery;
    }

    /**
     * Forces this dispatcher to quit immediately.  If any requests are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        if (DEBUG) VolleyLog.v("start new dispatcher");
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        while (dispatch()) {

        }
    }

    /**
     *
     * @return true to stay in the dispatch loop or false to quit
     */
    protected boolean dispatch() {
        try {
            // Get a request from the local triage queue, blocking until
            // at least one is available.
            final BallRequest request = mRequestQueue.take();
            request.addMarker("local-queue-take");

            // If the request has been canceled, don't bother dispatching it.
            if (request.isCanceled()) {
                request.finish("local-discard-canceled");
                return true;
            }

            Object responseContent = request.getLocalRequestProcessor().getLocalResponse();

            // Do not deliver local response if null
            // Let a chance to the cache to deliver a better response
            if (responseContent == null) {
                request.addMarker("local-response-content-null-exit");
                mDelivery.postEmptyIntermediateResponse(request, BallResponse.ResponseSource.LOCAL);
                return true;
            }

            request.addMarker("local-response-get-content-successful");

            BallResponse response = BallResponse.success(responseContent, null);
            response.setResponseSource(BallResponse.ResponseSource.LOCAL);
            response.setIntermediate(true);

            // Post the intermediate response back to the user and have
            // the delivery then forward the request along to the network.
            mDelivery.postResponse(request, response);

        } catch (InterruptedException e) {
            // We may have been interrupted because it was time to quit.
            if (mQuit) {
                return false;
            }
            return true;
        }

        return true;
    }
}