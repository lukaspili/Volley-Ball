package com.siu.android.volleyball.samples.volley.fake;

import com.android.volley.Cache;

/**
 * Created by lukas on 9/9/13.
 */
public class FakeCache implements Cache {

    private boolean mHit;
    private boolean mExpired;
    private boolean mSoftExpired;

    public FakeCache(boolean hit, boolean expired, boolean softExpired) {
        mHit = hit;
        mExpired = expired;
        mSoftExpired = softExpired;
    }

    @Override
    public Entry get(String key) {
        if (!mHit) {
            return null;
        }

        Entry entry = new Entry();

        if (!mExpired) {
            entry.ttl = System.currentTimeMillis() + 10000000 ;
        }

        if (!mSoftExpired) {
            entry.softTtl = System.currentTimeMillis() + 10000000 ;
        }

        return entry;
    }

    @Override
    public void put(String key, Entry entry) {

    }

    @Override
    public void initialize() {

    }

    @Override
    public void invalidate(String key, boolean fullExpire) {

    }

    @Override
    public void remove(String key) {

    }

    @Override
    public void clear() {

    }
}
