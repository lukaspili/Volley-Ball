package com.siu.android.volleyball.samples.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.response.ResponseListener;
import com.siu.android.volleyball.samples.Application;
import com.siu.android.volleyball.samples.R;
import com.siu.android.volleyball.samples.adapter.EntriesAdapter;
import com.siu.android.volleyball.samples.model.Entry;
import com.siu.android.volleyball.samples.util.SimpleLogger;
import com.siu.android.volleyball.samples.volley.CompleteEntryRequest;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ListView mListView;
    private EntriesAdapter mEntriesAdapter;

    private List<Entry> mEntries = new ArrayList<Entry>();

    private boolean mLoadingFromLocalFinished;
    private boolean mLoadingFromWebFinished;
    private boolean mLoadingFromWebSuccessful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main_activity);

        mListView = (ListView) findViewById(R.id.list);
        mEntriesAdapter = new EntriesAdapter(this, mEntries);
        mListView.setAdapter(mEntriesAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mLoadingFromWebSuccessful) {
            loadEntries();
        }
    }

    private void loadEntries() {
        setProgressBarIndeterminateVisibility(true);


        CompleteEntryRequest entryRequest = new CompleteEntryRequest(new ResponseListener<List<Entry>>() {
            @Override
            public void onIntermediateResponse(List<Entry> response, BallResponse.ResponseSource responseSource) {
                SimpleLogger.d("intermediate resposne %s", response);
            }

            @Override
            public void onFinalResponse(List<Entry> response, BallResponse.ResponseSource responseSource) {
                SimpleLogger.d("remote resposne %s", response);
                setProgressBarIndeterminateVisibility(false);
            }

            @Override
            public void onFinalResponseIdenticalToIntermediate(BallResponse.ResponseSource responseSource) {
                SimpleLogger.d("remote response identical to intermediate");
                setProgressBarIndeterminateVisibility(false);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SimpleLogger.d("error resposne %s", error.getMessage());
                setProgressBarIndeterminateVisibility(false);
            }
        }
        );

        Application.getRequestQueue().add(entryRequest);
    }
}
