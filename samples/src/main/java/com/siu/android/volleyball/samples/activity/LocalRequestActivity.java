package com.siu.android.volleyball.samples.activity;

import android.app.Activity;
import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.request.LocalRequest;
import com.siu.android.volleyball.response.ResponseListener;
import com.siu.android.volleyball.response.SingleResponseListener;
import com.siu.android.volleyball.samples.Application;
import com.siu.android.volleyball.samples.model.Entry;
import com.siu.android.volleyball.samples.util.SimpleLogger;
import com.siu.android.volleyball.samples.volley.request.CompleteEntryRequest;
import com.siu.android.volleyball.samples.volley.request.SampleLocalNoResultRequest;
import com.siu.android.volleyball.samples.volley.request.SampleLocalRequest;

import java.util.List;

public class LocalRequestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();

        startRequest();
    }

    private void startRequest() {
        // local request that returns something
        LocalRequest localRequest = new SampleLocalRequest(new SingleResponseListener<String>() {
            @Override
            public void onResponse(String response) {
                SimpleLogger.d("response from request %s", response);
            }
        });
        Application.getRequestQueue().add(localRequest);

        // local request that returns nothing
        LocalRequest localRequestWithoutResult = new SampleLocalNoResultRequest();
        Application.getRequestQueue().add(localRequestWithoutResult);
    }
}
