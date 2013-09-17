package com.siu.android.volleyball.samples.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.request.CompleteRequest;
import com.siu.android.volleyball.response.ResponseListener;

/**
 * Created by lukas on 9/13/13.
 */
public class SampleRequest extends CompleteRequest<Object> {

    public SampleRequest(int method, String url, ResponseListener<Object> responseListener, Response.ErrorListener errorListener) {
        super(method, url, responseListener, errorListener);
    }

    @Override
    protected Object getLocalResponse() {
        // query your local database for example
        // return the result or null if there is no result from database
        return new Object();
    }

    @Override
    public void saveNetworkResponseToLocal(Object response) {
        // save the network response to the local database
        // next time the request is performed the local response will return the result faster than the network request
    }

    @Override
    protected BallResponse<Object> parseBallNetworkResponse(NetworkResponse response) {
        // parse the result from the network request, in the same way than with volley
        return null;
    }
}
