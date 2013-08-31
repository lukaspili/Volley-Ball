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

import android.os.Handler;

import com.android.volley.VolleyError;

import java.util.concurrent.Executor;

/**
 * Delivers responses and errors.
 */
public class BallExecutorDelivery implements BallResponseDelivery {

    /**
     * Used for posting responses, typically to the main thread.
     */
    private final Executor mResponsePoster;

    /**
     * Creates a new response delivery interface.
     *
     * @param handler {@link android.os.Handler} to post responses on
     */
    public BallExecutorDelivery(final Handler handler) {
        // Make an Executor that just wraps the handler.
        mResponsePoster = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }

    /**
     * Creates a new response delivery interface, mockable version
     * for testing.
     *
     * @param executor For running delivery tasks
     */
    public BallExecutorDelivery(Executor executor) {
        mResponsePoster = executor;
    }

    @Override
    public void postResponse(BallRequest<?> request, BallResponse<?> response) {
        postResponse(request, response, null);
    }

    @Override
    public void postResponse(BallRequest<?> request, BallResponse<?> response, Runnable runnable) {
        request.markDelivered();
        request.addMarker("post-response");
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response, runnable));
    }

    @Override
    public void postError(BallRequest<?> request, VolleyError error) {
        request.addMarker("post-error");
        BallResponse<?> response = BallResponse.error(error);
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response, null));
    }

    /**
     * A Runnable used for delivering network responses to a listener on the
     * main thread.
     */
    @SuppressWarnings("rawtypes")
    private class ResponseDeliveryRunnable implements Runnable {
        private final BallRequest mRequest;
        private final BallResponse mResponse;
        private final Runnable mRunnable;

        public ResponseDeliveryRunnable(BallRequest request, BallResponse response, Runnable runnable) {
            mRequest = request;
            mResponse = response;
            mRunnable = runnable;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (mRequest.isFinished()) {
                return;
            }

            mRequest.addMarker("deliver-response-from-" + mResponse.getResponseSource().toString().toLowerCase());

            // no 2 intermediate responses, either local or cache
            if (mResponse.getResponseSource() == BallResponse.ResponseSource.LOCAL || mResponse.getResponseSource() == BallResponse.ResponseSource.CACHE) {
                if (mRequest.isIntermediateResponseDelivered()) {
                    continueRequest(mRequest, "intermediate-response-already-delivered-exit", mRunnable);
                    return;
                }

                mRequest.setIntermediateResponseDelivered(true);
            }
            // network response
            else {
                mRequest.setNetworkResponseDelivered(true);
            }

            if (mRequest.isCanceled()) {
                finishRequest(mRequest, "canceled-at-delivery", null);
                return;
            }

            // deliver local response
            if (mResponse.getResponseSource() == BallResponse.ResponseSource.LOCAL) {
                mRequest.getLocalRequestProcessor().deliverLocalResponse(mResponse.result);

                // deliver local response after a network response that failed
                if (mRequest.isNetworkResponseDelivered()) {
                    // finish the request if local response is the last one
                    finishRequest(mRequest, "done-with-response-from-local", mRunnable);
                    return;
                } else {
                    // otherwise local response is coming first
                    // mark the intermediate response if local is first response and let the request continue
                    mRequest.addMarker("intermediate-response");
                    return;
                }
            }
            // deliver cache or network response
            else {
                // deliver success or error
                if (mResponse.isSuccess()) {
                    mRequest.deliverResponse(mResponse.result);
                } else {
                    mRequest.deliverError(mResponse.error);
                }

                // continue or finish the request based on the response content
                if (mResponse.intermediate) {
                    // intermediate response from cache
                    continueRequest(mRequest, "intermediate-response", mRunnable);
                } else {
                    // response from network
                    if (!mResponse.isSuccess() && mRequest.shouldProcessLocal() && !mRequest.isIntermediateResponseDelivered()) {
                        // let the request continue if network response failed and there is a local request processing
                        // this scenario will happen if there is no cache policy and the intermediate response is delivered only from local
                        // processing that takes more time than the network response to be delivered
                        continueRequest(mRequest, "intermediate-error-response-from-network", mRunnable);
                    } else {
                        // in any other case, just finish the request
                        finishRequest(mRequest, "done-with-response-from-" + mResponse.getResponseSource().toString().toLowerCase(), mRunnable);
                    }
                }
            }
        }
    }

    public void continueRequest(BallRequest request, String tag, Runnable runnable) {
        request.addMarker(tag);
        runPostRunnableIfAny(runnable);
    }

    public void finishRequest(BallRequest request, String tag, Runnable runnable) {
        request.finish(tag);
        request.setFinished(true);
        runPostRunnableIfAny(runnable);
    }

    public void runPostRunnableIfAny(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }
}