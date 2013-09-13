package com.siu.android.volleyball.samples.activity.scenarios;

import com.siu.android.volleyball.BallRequestQueue;
import com.siu.android.volleyball.samples.volley.fake.FakeCache;
import com.siu.android.volleyball.samples.volley.fake.FakeNetwork;
import com.siu.android.volleyball.samples.volley.request.ScenarioRequest;
import com.siu.android.volleyball.toolbox.VolleyBall;
import com.siu.android.volleyball.toolbox.VolleyBallConfig;

/**
 * Scenario 10
 * <p/>
 * 1. Start the request
 * 2. Cache thread misses
 * 3. Network thread returns error response  -> wait for local response
 * 4. Local thread returns empty response -> post the network error response
 * 5. End
 */
public class Scenario10Activity extends ScenarioActivity {

    @Override
    protected BallRequestQueue buildRequestQueue() {
        return VolleyBall.newRequestQueue(new VolleyBallConfig.Builder(this)
                .cache(new FakeCache(false, false, false))
                .network(new FakeNetwork(false, false, 0))
                .build());
    }

    @Override
    protected void startRequest() {
        ScenarioRequest request = new ScenarioRequest(mListener, mErrorListener, 1, 0);
        request.setLocalResponse(null);

        mRequestQueue.add(request);
    }
}