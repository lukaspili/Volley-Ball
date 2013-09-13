package com.siu.android.volleyball.samples.activity.scenarios;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.siu.android.volleyball.BallRequestQueue;
import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.response.ResponseListener;
import com.siu.android.volleyball.samples.R;
import com.siu.android.volleyball.samples.adapter.LogsAdapter;
import com.siu.android.volleyball.samples.model.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class ScenarioActivity extends Activity {

    protected ListView mListView;
    protected LogsAdapter mAdapter;
    protected BallRequestQueue mRequestQueue;

    protected ResponseListener<String> mListener = new ResponseListener<String>() {
        @Override
        public void onIntermediateResponse(String response, BallResponse.ResponseSource responseSource) {
            printLog("intermediate response - " + responseSource);
        }

        @Override
        public void onFinalResponse(String response, BallResponse.ResponseSource responseSource) {
            printLog("final response - " + responseSource);
        }

        @Override
        public void onFinalResponseIdenticalToIntermediate(BallResponse.ResponseSource responseSource) {
            printLog("final identical response - " + responseSource);
        }
    };

    protected Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            printLog("final error response - NETWORK");
        }
    };

    protected List<Log> mList = new ArrayList<Log>();

    private boolean mLoadingFromLocalFinished;
    private boolean mLoadingFromWebFinished;
    private boolean mLoadingFromWebSuccessful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.scenario_activity);

        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new LogsAdapter(this, mList);
        mListView.setAdapter(mAdapter);

        mRequestQueue = buildRequestQueue();
    }

    @Override
    protected void onStart() {
        super.onStart();

        startRequest();
    }

    protected void printLog(String content) {
        Log log = new Log(content);
        mList.add(log);
        mAdapter.notifyDataSetChanged();
    }

    protected abstract BallRequestQueue buildRequestQueue();

    protected abstract void startRequest();

}
