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
import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Encapsulates a parsed response for delivery.
 *
 * @param <T> Parsed type of this response
 */
public class BallResponse<T> {

    public static enum ResponseSource {
        LOCAL, CACHE, NETWORK
    }

    protected Response<T> mResponse;
    protected ResponseSource mResponseSource;
    protected boolean mIdentical = false;

    /**
     * Returns whether this response is considered successful.
     */
    public boolean isSuccess() {
        return mResponse.isSuccess();
    }

    public static <T> BallResponse<T> identical(ResponseSource responseSource) {
        return new BallResponse<T>(Response.<T>success(null, null), responseSource, true);
    }

    public static <T> BallResponse<T> success(T result) {
        return new BallResponse<T>(Response.success(result, null));
    }

    public static <T> BallResponse<T> success(T result, Cache.Entry cacheEntry) {
        return new BallResponse<T>(Response.success(result, cacheEntry));
    }

    public static <T> BallResponse<T> error(VolleyError error) {
        return new BallResponse<T>(Response.<T>error(error), ResponseSource.NETWORK); // error cames always from network
    }

    protected BallResponse(Response<T> response) {
        this(response, null, false);
    }

    protected BallResponse(Response<T> response, ResponseSource responseSource) {
        this(response, responseSource, false);
    }

    public BallResponse(Response<T> response, ResponseSource responseSource, boolean identical) {
        mResponse = response;
        mResponseSource = responseSource;
        mIdentical = identical;
    }

    public ResponseSource getResponseSource() {
        return mResponseSource;
    }

    public void setResponseSource(ResponseSource responseSource) {
        this.mResponseSource = responseSource;
    }

    public boolean isIntermediate() {
        return mResponse.intermediate;
    }

    public void setIntermediate(boolean intermediate) {
        mResponse.intermediate = intermediate;
    }

    public T getResult() {
        return mResponse.result;
    }

    public Cache.Entry getCacheEntry() {
        return mResponse.cacheEntry;
    }

    public VolleyError getError() {
        return mResponse.error;
    }

    public boolean isIdentical() {
        return mIdentical;
    }

    public void setIdentical(boolean identical) {
        mIdentical = identical;
    }
}
