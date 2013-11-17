package com.siu.android.volleyball.response;

import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.exception.BallException;

/**
 * Created by lukas on 8/31/13.
 */
public abstract class SingleResponseListener<T> implements ResponseListener<T> {

    /**
     * Can happen when the response is soft cached
     *
     * @param response
     * @param responseSource
     */
    @Override
    public final void onIntermediateResponse(T response, BallResponse.ResponseSource responseSource) {
        onResponse(response);
    }

    @Override
    public final void onFinalResponse(T response, BallResponse.ResponseSource responseSource) {
        onResponse(response);
    }

    /**
     * Can happen when the response is soft cached. Maybe ?
     *
     * @param responseSource
     */
    @Override
    public final void onFinalResponseIdenticalToIntermediate(BallResponse.ResponseSource responseSource) {

    }

    //TODO: add response source because it can still be soft cached response
    public abstract void onResponse(T response);
}
