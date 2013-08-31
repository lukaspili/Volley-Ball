package com.siu.android.volleyball.samples;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
    private static SQLiteDatabase sSQLiteDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();

        // init volley ball
        VolleyBallConfig.Builder configBuilder = new VolleyBallConfig.Builder(sContext);

        // mock
        if (Constants.MOCK_WEBSERVICE) {
            FileMockNetwork network = new FileMockNetwork(sContext, new FileMockNetwork.Config()
                    .basePath("fakeapi")
                    .requestDuration(1)
                    .realNetworkHttpStack(new OkHttpStack()));
            configBuilder.network(network);
        } else {
            configBuilder.httpStack(new OkHttpStack());
        }

        sRequestQueue = VolleyBall.newRequestQueue(configBuilder.build());

        // init database helper
        sDatabaseHelper = new DatabaseHelper(sContext);
        sSQLiteDatabase = sDatabaseHelper.getWritableDatabase();
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

    public static SQLiteDatabase getSQLiteDatabase() {
        return sSQLiteDatabase;
    }
}
