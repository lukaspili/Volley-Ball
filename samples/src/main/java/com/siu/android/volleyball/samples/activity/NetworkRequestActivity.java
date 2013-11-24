package com.siu.android.volleyball.samples.activity;

import android.app.Activity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.siu.android.volleyball.request.NetworkRequest;
import com.siu.android.volleyball.response.SingleResponseListener;
import com.siu.android.volleyball.samples.Application;
import com.siu.android.volleyball.samples.util.SimpleLogger;
import com.siu.android.volleyball.samples.volley.request.SampleErrorNetworkRequest;
import com.siu.android.volleyball.samples.volley.request.SampleNetworkRequest;

public class NetworkRequestActivity extends Activity {

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
        NetworkRequest request = new SampleNetworkRequest(Request.Method.GET, "http://some.url1.com", new SingleResponseListener<String>() {
            @Override
            public void onResponse(String response) {
                SimpleLogger.d("response from request 1: %s", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SimpleLogger.d("error from request 1: %s", error.getMessage());
            }
        }
        );
        Application.getRequestQueue().add(request);
        SimpleLogger.d("Start request 1");

        request = new SampleErrorNetworkRequest(Request.Method.GET, "http://some.url2.com", new SingleResponseListener<String>() {
            @Override
            public void onResponse(String response) {
                SimpleLogger.d("response from request 2: %s", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SimpleLogger.d("error from request 2: %s", error.getMessage());
            }
        }
        );
        Application.getRequestQueue().add(request);
        SimpleLogger.d("Start request 2");
    }
}
