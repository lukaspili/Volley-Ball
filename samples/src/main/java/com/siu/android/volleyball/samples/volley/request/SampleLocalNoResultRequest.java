package com.siu.android.volleyball.samples.volley.request;

import com.siu.android.volleyball.request.LocalRequest;
import com.siu.android.volleyball.samples.util.ScenarioUtils;

/**
 * Sample local request
 * The local request that returns nothing
 */
public class SampleLocalNoResultRequest extends LocalRequest<Void> {

    public SampleLocalNoResultRequest() {
        super();
    }

    @Override
    public Void performLocal() {
        // perform your task outside of UI thread here
        ScenarioUtils.waitSeveralSeconds(2);

        // return nothing
        return null;
    }
}
