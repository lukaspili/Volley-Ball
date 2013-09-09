package com.siu.android.volleyball.samples.model;

import java.util.Date;

/**
 * Created by lukas on 9/3/13.
 */
public class Log {

    private long millis;
    private String mContent;

    public Log(String content) {
        mContent = content;
        millis = System.currentTimeMillis();
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }
}
