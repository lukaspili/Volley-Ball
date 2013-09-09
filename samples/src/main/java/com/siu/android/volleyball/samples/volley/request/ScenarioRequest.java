package com.siu.android.volleyball.samples.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.complete.CompleteRequest;
import com.siu.android.volleyball.response.ResponseListener;
import com.siu.android.volleyball.samples.util.ScenarioUtils;

/**
 * Created by lukas on 9/3/13.
 */
public class ScenarioRequest extends CompleteRequest<String> {

    public static final int METHOD = Method.GET;
    public static final String URL = "http://foo.com/bar";

    protected int mLocalWait;
    protected int mNetworkWait;

    public ScenarioRequest(ResponseListener<String> responseListener, Response.ErrorListener errorListener, int localWait, int networkWait) {
        super(METHOD, URL, responseListener, errorListener);

        mLocalWait = localWait;
        mNetworkWait = networkWait;
    }

    @Override
    protected String getLocalResponse() {
        ScenarioUtils.wait(mLocalWait);
        return "response";
    }

    @Override
    public void saveLocalResponse(String response) {
        // do nothing
    }

    @Override
    protected BallResponse<String> parseBallNetworkResponse(NetworkResponse response) {
        ScenarioUtils.wait(mNetworkWait);
        return BallResponse.success("response");
    }
}
