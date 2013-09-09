package com.siu.android.volleyball.toolbox;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.siu.android.volleyball.util.ConfigUtils;

import java.io.File;

/**
 * Created by lukas on 8/29/13.
 */
public class VolleyBallConfig {

    private static final String DEFAULT_CACHE_DIR = "volley";

    private Context mContext;
    private HttpStack mHttpStack;
    private Network mNetwork;
    private Cache mCache;

    private VolleyBallConfig() {
    }

    public static class Builder {
        private VolleyBallConfig mInstance;

        public Builder(Context context) {
            mInstance = new VolleyBallConfig();
            mInstance.mContext = context;
        }

        public Builder httpStack(HttpStack httpStack) {
            mInstance.mHttpStack = httpStack;
            return this;
        }

        public Builder network(Network network) {
            mInstance.mNetwork = network;
            return this;
        }

        public Builder cache(Cache cache) {
            mInstance.mCache = cache;
            return this;
        }

        public VolleyBallConfig build() {
            if (mInstance.mHttpStack == null) {
                mInstance.mHttpStack = ConfigUtils.getDefaultHttpStack(mInstance.mContext);
            }

            if (mInstance.mNetwork == null) {
                mInstance.mNetwork = ConfigUtils.getDefaultNetwork(mInstance.mHttpStack);
            }

            if (mInstance.mCache == null) {
                File cacheDir = new File(mInstance.mContext.getCacheDir(), DEFAULT_CACHE_DIR);
                mInstance.mCache = new DiskBasedCache(cacheDir);
            }

            return mInstance;
        }
    }


    public Context getContext() {
        return mContext;
    }

    public HttpStack getHttpStack() {
        return mHttpStack;
    }

    public Network getNetwork() {
        return mNetwork;
    }

    public Cache getCache() {
        return mCache;
    }
}
