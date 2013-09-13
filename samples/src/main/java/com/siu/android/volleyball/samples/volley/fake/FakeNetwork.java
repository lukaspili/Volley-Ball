package com.siu.android.volleyball.samples.volley.fake;

import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.siu.android.volleyball.samples.util.ScenarioUtils;

import java.util.HashMap;

/**
 * Created by lukas on 9/9/13.
 */
public class FakeNetwork implements Network {

    private boolean mSuccess = true;
    private boolean mNotModified = false;
    private int mWaitSeconds = 0;

    public FakeNetwork() {
    }

    public FakeNetwork(boolean success, boolean notModified, int waitSeconds) {
        mSuccess = success;
        mNotModified = notModified;
        mWaitSeconds = waitSeconds;
    }

    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        ScenarioUtils.waitSeveralSeconds(mWaitSeconds);

        if (mSuccess) {
            int code = mNotModified ? 304 : 200;
            return new NetworkResponse(code, new byte[0], new HashMap<String, String>(), mNotModified);
        }

        throw new VolleyError(new NetworkResponse(404, new byte[0], new HashMap<String, String>(), mNotModified));
    }
}
