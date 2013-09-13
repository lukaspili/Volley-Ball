package com.siu.android.volleyball.samples.activity.scenarios;

import com.siu.android.volleyball.BallRequestQueue;
import com.siu.android.volleyball.samples.volley.fake.FakeCache;
import com.siu.android.volleyball.samples.volley.fake.FakeNetwork;
import com.siu.android.volleyball.samples.volley.request.ScenarioRequest;
import com.siu.android.volleyball.toolbox.VolleyBall;
import com.siu.android.volleyball.toolbox.VolleyBallConfig;

/**
 * Scenario 7
 * <p/>
 * 1. Start the request
 * 2. Cache thread hits soft cache -> post an intermediate response
 * 3. Local thread returns valid response -> intermediate response ignored
 * 4. Network thread returns valid response -> post a final response
 * 5. End
 */
public class Scenario7Activity extends ScenarioActivity {

    @Override
    protected BallRequestQueue buildRequestQueue() {
        return VolleyBall.newRequestQueue(new VolleyBallConfig.Builder(this)
                .cache(new FakeCache(true, false, true))
                .network(new FakeNetwork(true, false, 2))
                .build());
    }

    @Override
    protected void startRequest() {
        mRequestQueue.add(new ScenarioRequest(mListener, mErrorListener, 1, 0));
    }
}