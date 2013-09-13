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
    protected int mCacheAndNetworkWait;
    protected String mLocalResponse = "response";

    public ScenarioRequest(ResponseListener<String> responseListener, Response.ErrorListener errorListener, int localWait, int cacheAndNetworkWait) {
        super(METHOD, URL, responseListener, errorListener);

        mLocalWait = localWait;
        mCacheAndNetworkWait = cacheAndNetworkWait;
    }

    @Override
    protected String getLocalResponse() {
        ScenarioUtils.waitSeveralSeconds(mLocalWait);
        return mLocalResponse;
    }

    @Override
    public void saveNetworkResponseToLocal(String response) {
        // do nothing
    }

    @Override
    protected BallResponse<String> parseBallNetworkResponse(NetworkResponse response) {
        ScenarioUtils.waitSeveralSeconds(mCacheAndNetworkWait);
        return BallResponse.success("response");
    }

    public void setLocalResponse(String localResponse) {
        mLocalResponse = localResponse;
    }
}
