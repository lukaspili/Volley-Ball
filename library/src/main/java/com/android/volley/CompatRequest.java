package com.android.volley;

import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.request.NetworkRequest;

/**
 * Not finished
 */
public class CompatRequest<T> extends NetworkRequest<T> {

    private Request<T> mRequest;

    public CompatRequest(Request request) {
        super(request.getMethod(), request.getUrl(), null);
    }

    @Override
    protected BallResponse<T> parseBallNetworkResponse(NetworkResponse networkResponse) {
        Response<T> response = mRequest.parseNetworkResponse(networkResponse);
        return response.isSuccess() ? BallResponse.success(response.result, response.cacheEntry) : BallResponse.<T>error(response.error);
    }
}
