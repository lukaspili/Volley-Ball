package com.siu.android.volleyball.samples.volley;

import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.response.ResponseListener;
import com.siu.android.volleyball.samples.adapter.LogsAdapter;

import java.util.List;

/**
 * Created by lukas on 9/3/13.
 */
public class ScenarioListener implements ResponseListener<String> {

    private List<String> mList;
    private LogsAdapter mAdapter;

    public ScenarioListener(List<String> list, LogsAdapter adapter) {
        mList = list;
        mAdapter = adapter;
    }

    @Override
    public void onIntermediateResponse(String response, BallResponse.ResponseSource responseSource) {
        mList.add(response);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFinalResponse(String response, BallResponse.ResponseSource responseSource) {
        mList.add(response);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFinalResponseIdenticalToIntermediate(BallResponse.ResponseSource responseSource) {
        mList.add("identical response");
        mAdapter.notifyDataSetChanged();
    }
}
