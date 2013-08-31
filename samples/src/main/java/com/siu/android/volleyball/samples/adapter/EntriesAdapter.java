package com.siu.android.volleyball.samples.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.siu.android.volleyball.samples.R;
import com.siu.android.volleyball.samples.model.Entry;

import java.util.List;

/**
 * Created by lukas on 8/29/13.
 */
public class EntriesAdapter extends BindableAdapter<Entry> {

    private List<Entry> mEntries;

    public EntriesAdapter(Context context, List<Entry> entries) {
        super(context);
        mEntries = entries;
    }

    @Override
    public Entry getItem(int position) {
        return mEntries.get(position);
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.row, null);
    }

    @Override
    public void bindView(Entry item, int position, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        titleTextView.setText(item.getTitle());
    }

    @Override
    public int getCount() {
        return mEntries.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
