package com.siu.android.volleyball.mock;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.siu.android.volleyball.BallRequest;
import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.response.ResponseListener;

/**
 * Created by lukas on 9/1/13.
 */
public class BallRequestMock extends BallRequest {

    public BallRequestMock() {
        super(Method.GET, "http://www.google.com/foobar", new ResponseListener() {
                    @Override
                    public void onIntermediateResponse(Object response, BallResponse.ResponseSource responseSource) {

                    }

                    @Override
                    public void onFinalResponse(Object response, BallResponse.ResponseSource responseSource) {

                    }

                    @Override
                    public void onFinalResponseIdenticalToIntermediate(BallResponse.ResponseSource responseSource) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
    }
}
