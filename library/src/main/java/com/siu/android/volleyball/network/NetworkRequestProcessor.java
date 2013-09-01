package com.siu.android.volleyball.network;

import com.android.volley.NetworkResponse;
import com.siu.android.volleyball.BallResponse;

/**
 * Created by lukas on 8/31/13.
 */
public interface NetworkRequestProcessor<T> {

    public abstract BallResponse<T> parseNetworkResponse(NetworkResponse networkResponse);
}
