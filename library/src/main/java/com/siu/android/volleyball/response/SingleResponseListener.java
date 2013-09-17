package com.siu.android.volleyball.response;

import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.exception.BallException;

/**
 * Created by lukas on 8/31/13.
 */
public abstract class SingleResponseListener<T> implements ResponseListener<T> {

    @Override
    public final void onIntermediateResponse(T response, BallResponse.ResponseSource responseSource) {
        throw new BallException("Single response listener does not have intermediate response");
    }

    @Override
    public final void onFinalResponse(T response, BallResponse.ResponseSource responseSource) {
        onResponse(response);
    }

    @Override
    public final void onFinalResponseIdenticalToIntermediate(BallResponse.ResponseSource responseSource) {
        throw new BallException("Single response listener does not have intermediate response");
    }

    public abstract void onResponse(T response);
}
