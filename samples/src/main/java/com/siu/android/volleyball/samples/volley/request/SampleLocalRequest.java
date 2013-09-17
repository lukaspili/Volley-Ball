package com.siu.android.volleyball.samples.volley.request;

import com.android.volley.Response;
import com.siu.android.volleyball.request.LocalRequest;
import com.siu.android.volleyball.response.ResponseListener;
import com.siu.android.volleyball.samples.util.ScenarioUtils;

/**
 * Sample local request
 * The request here returns a String object result of the async operation, but you can also use the Void type
 */
public class SampleLocalRequest extends LocalRequest<String> {

    public SampleLocalRequest(ResponseListener<String> responseListener) {
        super(responseListener);
    }

    @Override
    public String performLocal() {
        // perform your task outside of UI thread here
        ScenarioUtils.waitSeveralSeconds(2);

        // you can return some result or null
        return "result";
    }
}
