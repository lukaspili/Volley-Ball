package com.siu.android.volleyball.toolbox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Network;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

/**
 * Created by lukas on 8/29/13.
 */
public class VolleyBallConfig {

    private Context mContext;
    private HttpStack mHttpStack;
    private Network mNetwork;

    private VolleyBallConfig() {
    }

    public static class Builder {
//        private Context mContext;
//        private HttpStack mHttpStack;
//        private Network mNetwork;
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

        public VolleyBallConfig build() {
            if (mInstance.mHttpStack == null) {
                mInstance.mHttpStack = getDefaultHttpStack(mInstance.mContext);
            }

            if (mInstance.mNetwork == null) {
                mInstance.mNetwork = getDefaultNetwork(mInstance.mHttpStack);
            }

            return mInstance;
        }

        private HttpStack getDefaultHttpStack(Context context) {
            String userAgent = "volley/0";
            try {
                String packageName = context.getPackageName();
                PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
                userAgent = packageName + "/" + info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
            }

            HttpStack stack;
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }

            return stack;
        }

        private Network getDefaultNetwork(HttpStack httpStack) {
            return new BasicNetwork(httpStack);
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
}
