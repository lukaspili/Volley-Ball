package com.siu.android.volleyball.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.siu.android.volleyball.BallRequest;
import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.network.NetworkRequestProcessor;
import com.siu.android.volleyball.response.ResponseListener;
import com.siu.android.volleyball.response.SingleResponseListener;

/**
 * Created by lukas on 9/16/13.
 */
public abstract class NetworkRequest<T> extends BallRequest<T> {

    protected NetworkRequest(int method, String url, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
    }

    public NetworkRequest(int method, String url, SingleResponseListener<T> responseListener, Response.ErrorListener errorListener) {
        super(method, url, responseListener, errorListener);
    }

    @Override
    public boolean shouldProcessNetwork() {
        return true;
    }

    @Override
    protected NetworkRequestProcessor createNetworkRequestProcessor() {
        return new NetworkRequestProcessor() {
            @Override
            public BallResponse<T> parseNetworkResponse(NetworkResponse networkResponse) {
                return parseBallNetworkResponse(networkResponse);
            }
        };
    }

    protected abstract BallResponse<T> parseBallNetworkResponse(NetworkResponse response);
}
