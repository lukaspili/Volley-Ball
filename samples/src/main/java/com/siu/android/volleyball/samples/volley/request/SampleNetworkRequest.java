package com.siu.android.volleyball.samples.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.request.NetworkRequest;
import com.siu.android.volleyball.response.SingleResponseListener;

/**
 * Created by lukas on 9/17/13.
 */
public class SampleNetworkRequest extends NetworkRequest<String> {

    public SampleNetworkRequest(int method, String url, SingleResponseListener<String> responseListener, Response.ErrorListener errorListener) {
        super(method, url, responseListener, errorListener);
    }

    @Override
    protected BallResponse<String> parseBallNetworkResponse(NetworkResponse response) {
        return BallResponse.success("some response that you would parse from response.data", HttpHeaderParser.parseCacheHeaders(response));
    }
}
