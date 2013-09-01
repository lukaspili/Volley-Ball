/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.siu.android.volleyball;

import android.net.TrafficStats;
import android.os.Build;
import android.os.Process;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.ResponseDelivery;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import java.util.concurrent.BlockingQueue;

/**
 * Provides a thread for performing network dispatch from a queue of requests.
 * <p/>
 * Requests added to the specified queue are processed from the network via a
 * specified {@link com.android.volley.Network} interface. Responses are committed to cache, if
 * eligible, using a specified {@link com.android.volley.Cache} interface. Valid responses and
 * errors are posted back to the caller via a {@link com.android.volley.ResponseDelivery}.
 */
@SuppressWarnings("rawtypes")
public class BallNetworkDispatcher extends Thread {
    /**
     * The queue of requests to service.
     */
    private final BlockingQueue<BallRequest> mQueue;
    /**
     * The network interface for processing requests.
     */
    private final Network mNetwork;
    /**
     * The cache to write to.
     */
    private final Cache mCache;
    /**
     * For posting responses and errors.
     */
    private final BallResponseDelivery mDelivery;
    /**
     * Used for telling us to die.
     */
    private volatile boolean mQuit = false;

    /**
     * Creates a new network dispatcher thread.  You must call {@link #start()}
     * in order to begin processing.
     *
     * @param queue    Queue of incoming requests for triage
     * @param network  Network interface to use for performing requests
     * @param cache    Cache interface to use for writing responses to cache
     * @param delivery Delivery interface to use for posting responses
     */
    public BallNetworkDispatcher(BlockingQueue<BallRequest> queue,
                                 Network network, Cache cache,
                                 BallResponseDelivery delivery) {
        mQueue = queue;
        mNetwork = network;
        mCache = cache;
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
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        BallRequest request;
        while (true) {
            try {
                // Take a request from the queue.
                request = mQueue.take();
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
                continue;
            }

            try {
                request.addMarker("network-queue-take");

                // If the request was cancelled already, do not perform the
                // network request.
                if (request.isCanceled()) {
                    request.finish("network-discard-cancelled");
                    continue;
                }

                // Tag the request (if API >= 14)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    TrafficStats.setThreadStatsTag(request.getTrafficStatsTag());
                }

                // Perform the network request.
                NetworkResponse networkResponse = mNetwork.performRequest(request);
                request.addMarker("network-http-complete");

                // If the server returned 304 AND we delivered a response already,
                // we're done -- don't deliver a second identical response.
//                if (networkResponse.notModified && request.isIntermediateResponseDelivered()) { //request.hasHadResponseDelivered()) {
//                    BallResponse response = BallResponse.identical(BallResponse.ResponseSource.NETWORK);
//                    mDelivery.postResponse(request, response);
//                    request.finish("not-modified");
//                    continue;
//                }

                // Parse the response here on the worker thread.
                BallResponse<?> response = request.getNetworkRequestProcessor().parseNetworkResponse(networkResponse);
                response.setResponseSource(BallResponse.ResponseSource.NETWORK);
                request.addMarker("network-parse-complete");

                //TODO: Don't parse network response for 304
                if (networkResponse.notModified) {
                    response.setIdentical(true);
                    request.addMarker("not-modified");
                }

                // Write to cache if applicable.
                // TODO: Only update cache metadata instead of entire record for 304s.
                if (request.shouldCache() && response.getCacheEntry() != null) {
                    mCache.put(request.getCacheKey(), response.getCacheEntry());
                    request.addMarker("network-cache-written");
                }

                // Post the response back.
//                request.markDelivered();
                mDelivery.postResponse(request, response);

                // save to local if applicable
                if (request.shouldProcessLocal()) {
                    request.getLocalRequestProcessor().saveLocalResponse(response.getResult());
                }

            } catch (VolleyError volleyError) {
                parseAndDeliverNetworkError(request, volleyError);
            } catch (Exception e) {
                VolleyLog.e(e, "Unhandled exception %s", e.toString());
                mDelivery.postError(request, new VolleyError(e));
            }
        }
    }

    private void parseAndDeliverNetworkError(BallRequest<?> request, VolleyError error) {
        error = request.parseNetworkError(error);
        mDelivery.postError(request, error);
    }

    /**
     * Write to local database if applicable
     *
     * @param request the request
     * @param response the response
     */
    private void processLocalIfApplicable(BallRequest request, BallResponse response) {
        if (request.shouldProcessLocal()) {
            request.getLocalRequestProcessor().saveLocalResponse(response.getResult());
        }
    }
}
