package com.siu.android.volleyball.samples.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.siu.android.volleyball.samples.R;
import com.siu.android.volleyball.samples.model.Log;

import java.util.List;

/**
 * Created by lukas on 8/29/13.
 */
public class LogsAdapter extends BindableAdapter<Log> {

    private List<Log> mList;

    public LogsAdapter(Context context, List<Log> list) {
        super(context);
        mList = list;
    }

    @Override
    public Log getItem(int position) {
        return mList.get(position);
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.log_row, null);
    }

    @Override
    public void bindView(Log item, int position, View view) {
        TextView contentTextView = (TextView) view.findViewById(R.id.content);
        contentTextView.setText(item.getContent());

        TextView millisTextView = (TextView) view.findViewById(R.id.millis);
        millisTextView.setText(String.valueOf(item.getMillis()));
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
