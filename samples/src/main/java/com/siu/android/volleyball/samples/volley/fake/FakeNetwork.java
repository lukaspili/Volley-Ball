package com.siu.android.volleyball.samples.volley.fake;

import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.util.HashMap;

/**
 * Created by lukas on 9/9/13.
 */
public class FakeNetwork implements Network {

    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        return new NetworkResponse(200, new byte[0], new HashMap<String, String>(), false);
    }
}
