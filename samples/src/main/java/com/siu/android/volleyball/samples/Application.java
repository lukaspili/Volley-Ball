package com.siu.android.volleyball.samples;

import android.content.Context;

import com.siu.android.volleyball.BallRequestQueue;
import com.siu.android.volleyball.mock.FileMockNetwork;
import com.siu.android.volleyball.samples.database.DatabaseHelper;
import com.siu.android.volleyball.samples.volley.OkHttpStack;
import com.siu.android.volleyball.toolbox.VolleyBall;
import com.siu.android.volleyball.toolbox.VolleyBallConfig;

/**
 * Created by lukas on 8/29/13.
 */
public class Application extends android.app.Application {

    private static Context sContext;
    private static BallRequestQueue sRequestQueue;
    private static DatabaseHelper sDatabaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();

        // init volley ball
        VolleyBallConfig.Builder configBuilder = new VolleyBallConfig.Builder(sContext)
                .httpStack(new OkHttpStack());

        if (Constants.MOCK_WEBSERVICE) {
            configBuilder.network(new FileMockNetwork());
        }

        sRequestQueue = VolleyBall.newRequestQueue(configBuilder.build());

        // init database helper
        sDatabaseHelper = new DatabaseHelper(sContext);
    }

    public static Context getContext() {
        return sContext;
    }

    public static BallRequestQueue getRequestQueue() {
        return sRequestQueue;
    }

    public static DatabaseHelper getDatabaseHelper() {
        return sDatabaseHelper;
    }
}
