package com.siu.android.volleyball.samples.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.siu.android.volleyball.samples.R;
import com.siu.android.volleyball.samples.adapter.EntriesAdapter;
import com.siu.android.volleyball.samples.model.Entry;

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
        setContentView(R.layout.main_activity);

        mListView = (ListView) findViewById(R.id.list);
        mEntriesAdapter = new EntriesAdapter(this, mEntries);
        mListView.setAdapter(mEntriesAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!mLoadingFromWebSuccessful) {

        }
    }

    private void loadEntries() {

    }
}
