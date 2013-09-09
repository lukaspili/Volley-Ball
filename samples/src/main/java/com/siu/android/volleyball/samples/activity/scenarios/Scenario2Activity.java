package com.siu.android.volleyball.samples.activity.scenarios;

import com.siu.android.volleyball.BallRequestQueue;
import com.siu.android.volleyball.samples.activity.ScenarioActivity;
import com.siu.android.volleyball.samples.volley.fake.FakeCache;
import com.siu.android.volleyball.samples.volley.fake.FakeNetwork;
import com.siu.android.volleyball.samples.volley.request.ScenarioRequest;
import com.siu.android.volleyball.toolbox.VolleyBall;
import com.siu.android.volleyball.toolbox.VolleyBallConfig;

/**
 * Scenario 2
 * <p/>
 * 1. Start the request
 * 2. Cache thread hits and returns a valid response -> post a final response
 * 3. Local thread returns valid response -> ignored
 * 4. End
 */
public class Scenario2Activity extends ScenarioActivity {

    @Override
    protected BallRequestQueue buildRequestQueue() {
        return VolleyBall.newRequestQueue(new VolleyBallConfig.Builder(this)
                .cache(new FakeCache(true, false, false))
                .network(new FakeNetwork())
                .build());
    }

    @Override
    protected void startRequest() {
        mRequestQueue.add(new ScenarioRequest(mListener, mErrorListener, 1, 0));
    }
}