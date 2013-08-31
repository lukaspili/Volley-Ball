package com.siu.android.volleyball.samples.model;

import com.siu.android.volleyball.samples.database.mapping.EntryMapping;

/**
 * Created by lukas on 8/29/13.
 */
public class Entry implements EntryMapping {

    private long id;
    private String title;

    public Entry() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
