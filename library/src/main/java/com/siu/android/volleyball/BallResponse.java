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

import com.android.volley.Cache;
import com.android.volley.VolleyError;

/**
 * Encapsulates a parsed response for delivery.
 *
 * @param <T> Parsed type of this response
 */
public class BallResponse<T> {

    public interface Listener<T> {
        public void onResponse(T response);
    }

    public interface ErrorListener {
        public void onErrorResponse(VolleyError error);
    }

    public interface ListenerWithLocalProcessing<T> {
        public void onLocalResponse(T response);

        public void onRemoteResponse(T response);
    }

    public static enum ResponseSource {
        LOCAL, CACHE, NETWORK
    }

    protected ResponseSource mResponseSource;

    /**
     * Parsed response, or null in the case of error.
     */
    public final T result;

    /**
     * Cache metadata for this response, or null in the case of error.
     */
    public final Cache.Entry cacheEntry;

    /**
     * Detailed error information if <code>errorCode != OK</code>.
     */
    public final VolleyError error;

    /**
     * True if this response was a soft-expired one and a second one MAY be coming.
     */
    public boolean intermediate = false;

    /**
     * Returns whether this response is considered successful.
     */
    public boolean isSuccess() {
        return error == null;
    }


    public static <T> BallResponse<T> success(T result, Cache.Entry cacheEntry) {
        return new BallResponse<T>(result, cacheEntry);
    }

    public static <T> BallResponse<T> error(VolleyError error) {
        return new BallResponse<T>(error);
    }

    protected BallResponse(T result, Cache.Entry cacheEntry) {
        this.result = result;
        this.cacheEntry = cacheEntry;
        this.error = null;
    }

    protected BallResponse(VolleyError error) {
        this.result = null;
        this.cacheEntry = null;
        this.error = error;

        mResponseSource = ResponseSource.NETWORK; // error cames always from network
    }

    public ResponseSource getResponseSource() {
        return mResponseSource;
    }

    public void setResponseSource(ResponseSource responseSource) {
        this.mResponseSource = responseSource;
    }
}
