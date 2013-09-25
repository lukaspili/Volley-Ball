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

package com.siu.android.volleyball.ball;

import android.os.Handler;

import com.android.volley.VolleyError;
import com.siu.android.volleyball.BallRequest;
import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.BallResponseDelivery;
import com.siu.android.volleyball.exception.BallException;

import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Delivers responses and errors following the Ball request lifecycle of intermediate and final responses.
 */
public class BallExecutorDelivery implements BallResponseDelivery {

    public static final String MARKER_POST_EMPTY_INTERMEDIATE_RESPONSE = "post-empty-intermediate-response";
    public static final String MARKER_POST_RESPONSE = "post-response";
    public static final String MARKER_POST_ERROR = "post-error";
    public static final String MARKER_INTERMEDIATE_RESPONSE_ALREADY_DELIVERED = "intermediate-response-already-delivered-exit";
    public static final String MARKER_DONE_WITH_RESPONSE_FROM = "done-with-response-from-%s";
    public static final String MARKER_ERROR_IN_FINAL_RESPONSE_LET_INTERMEDIATE_CONTINUE = "error-in-final-response-let-intermediate-continue";
    public static final String MARKER_DONE_WITH_INTERMEDIATE_EMPTY_RESPONSE = "done-with-intermediate-empty-response";
    public static final String MARKER_DONE_WITH_INTERMEDIATE_RESPONSE = "done-with-intermediate-response";

    private final PriorityBlockingQueue<BallRequest> mNetworkQueue;

    /**
     * Used for posting responses, typically to the main thread.
     */
    private final Executor mResponsePoster;

    /**
     * Creates a new response delivery interface.
     *
     * @param handler {@link android.os.Handler} to post responses on
     */
    public BallExecutorDelivery(final Handler handler, PriorityBlockingQueue<BallRequest> networkQueue) {
        // Make an Executor that just wraps the handler.
        mResponsePoster = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
        mNetworkQueue = networkQueue;
    }

    /**
     * Creates a new response delivery interface, mockable version
     * for testing.
     *
     * @param executor For running delivery tasks
     */
    public BallExecutorDelivery(Executor executor) {
        mResponsePoster = executor;
        mNetworkQueue = null;
    }

    @Override
    public void postResponse(BallRequest<?> request, BallResponse<?> response) {
        request.addMarker(MARKER_POST_RESPONSE);
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response, null));
    }

    @Override
    public void postResponseAndForwardToNetwork(BallRequest<?> request, BallResponse<?> response) {
        request.addMarker(MARKER_POST_RESPONSE);
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response, mNetworkQueue));
    }
//
//    @Override
//    public void postResponse(BallRequest<?> request, BallResponse<?> response, Runnable runnable) {
//        //request.markDelivered();
//        request.addMarker(MARKER_POST_RESPONSE);
//        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response, runnable));
//    }

    @Override
    public void postError(BallRequest<?> request, VolleyError error) {
        request.addMarker(MARKER_POST_ERROR);
        BallResponse<?> response = BallResponse.error(error);
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response, null));
    }

    @Override
    public void postEmptyIntermediateResponse(BallRequest request, BallResponse.ResponseSource responseSource) {
        request.addMarker(MARKER_POST_EMPTY_INTERMEDIATE_RESPONSE);
        mResponsePoster.execute(new EmptyIntermediateDeliveryRunnable(request, responseSource, mNetworkQueue));
    }

    protected static class EmptyIntermediateDeliveryRunnable implements Runnable {
        protected final BallRequest mRequest;
        protected final BallResponse.ResponseSource mResponseSource;
        protected final PriorityBlockingQueue<BallRequest> mNetworkQueue;

        private EmptyIntermediateDeliveryRunnable(BallRequest request, BallResponse.ResponseSource responseSource, PriorityBlockingQueue<BallRequest> networkQueue) {
            mRequest = request;
            mResponseSource = responseSource;
            mNetworkQueue = networkQueue;
        }

        @Override
        public void run() {
            if (mRequest.isFinished() || mRequest.areAllIntermediateResponsesDelivered()) {
                return;
            }

            mRequest.markIntermediateResponseDelivered(mResponseSource);

            // final response already delivered,
            if (mRequest.isFinalResponseDelivered()) {
                if (mRequest.getFinalResponseError() == null) {
                    throw new BallException("Final response error can't be null when intermediate response is the last delivered response");
                }

                mRequest.deliverError(mRequest.getFinalResponseError());
                mRequest.finish(MARKER_DONE_WITH_INTERMEDIATE_EMPTY_RESPONSE); //TODO: ADD SOURCE ?
            }

            // else forward network if need
            else if (mNetworkQueue != null) {
                mNetworkQueue.put(mRequest);
            }
        }
    }


    @SuppressWarnings("rawtypes")
    protected static class ResponseDeliveryRunnable implements Runnable {
        protected final BallRequest mRequest;
        protected final BallResponse mResponse;
        protected final PriorityBlockingQueue<BallRequest> mNetworkQueue;

        public ResponseDeliveryRunnable(BallRequest request, BallResponse response, PriorityBlockingQueue<BallRequest> networkQueue) {
            mRequest = request;
            mResponse = response;
            mNetworkQueue = networkQueue;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            // this may happen because responses can be delivered from 2 parallel worker threads, the local and the cache/network one
            if (mRequest.isFinished()) {
                return;
            }

            mRequest.addMarker("deliver-response-from-" + mResponse.getResponseSource().toString().toLowerCase());

            if (mRequest.isCanceled()) {
                mRequest.finish("canceled-at-delivery");
                return;
            }

            // intermediate response from local or cache
            if (mResponse.isIntermediate()) {
                if (mRequest.isIntermediateResponseDeliveredWithSuccess()) {
                    mRequest.addMarker(MARKER_INTERMEDIATE_RESPONSE_ALREADY_DELIVERED);
                    return;
                }

                mRequest.setIntermediateResponseDeliveredWithSuccess(true);
                mRequest.markIntermediateResponseDelivered(mResponse.getResponseSource());

                // errors come only from network response, we don't have error management for local or cache responses
                if (!mResponse.isSuccess()) {
                    throw new BallException("Error response must come only from network, thus they can't be intermediate");
                }

                mRequest.deliverIntermediateResponse(mResponse.getResult(), mResponse.getResponseSource());

                // intermediate response coming from local while the network response was already delivered as an error
                if (mRequest.isFinalResponseDelivered()) {
                    if (mRequest.getFinalResponseError() == null) {
                        throw new BallException("Final response error can't be null when intermediate response is the last delivered response");
                    }

                    // deliver the error after the intermediate response to respect the delivering lifecycle
                    mRequest.deliverError(mRequest.getFinalResponseError());
                    mRequest.finish(MARKER_DONE_WITH_INTERMEDIATE_RESPONSE);
                } else {
                    if (mNetworkQueue != null) {
                        mNetworkQueue.put(mRequest);
                    }
                }
            }

            // final response from cache or network
            else {
                mRequest.setFinalResponseDelivered(true);

                if (mResponse.isSuccess()) {
                    if (mResponse.isIdentical()) {
                        // so far identical can only come from network, but it should be able to come from cache as well
                        mRequest.deliverIdenticalFinalResponse(mResponse.getResponseSource());
                    } else {
                        mRequest.deliverFinalResponse(mResponse.getResult(), mResponse.getResponseSource());
                    }

                } else {
                    if (mRequest.areAllIntermediateResponsesDelivered()) {
                        mRequest.deliverError(mResponse.getError());
                    } else {
                        // let the request continue if network response failed and there is a local request processing
                        // this scenario will happen if there is no cache policy and the intermediate response is delivered only from local
                        // processing that takes more time than the network response to be delivered
                        mRequest.addMarker(MARKER_ERROR_IN_FINAL_RESPONSE_LET_INTERMEDIATE_CONTINUE);
                        mRequest.setFinalResponseError(mResponse.getError());
                        return;
                    }
                }

                // after final response, finish the request (except for the case of intermediate response still to be delivered)
                mRequest.finish(String.format(MARKER_DONE_WITH_RESPONSE_FROM, mResponse.getResponseSource().toString().toLowerCase()));
            }
        }
    }


}