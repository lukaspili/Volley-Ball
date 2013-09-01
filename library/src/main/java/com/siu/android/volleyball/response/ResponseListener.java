package com.siu.android.volleyball.response;

import com.siu.android.volleyball.BallResponse;

/**
 * Created by lukas on 8/31/13.
 */
public interface ResponseListener<T> {

    public void onIntermediateResponse(T response, BallResponse.ResponseSource responseSource);

    public void onFinalResponse(T response, BallResponse.ResponseSource responseSource);

    public void onFinalResponseIdenticalToIntermediate(BallResponse.ResponseSource responseSource);
}
