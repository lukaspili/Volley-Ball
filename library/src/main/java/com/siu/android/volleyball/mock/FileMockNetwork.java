package com.siu.android.volleyball.mock;

import android.content.Context;

import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.siu.android.volleyball.exception.BallException;
import com.siu.android.volleyball.util.BallLogger;
import com.siu.android.volleyball.util.ConfigUtils;
import com.siu.android.volleyball.util.RequestUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lukas on 8/29/13.
 */
public class FileMockNetwork implements Network {


    private Context mContext;
    private Config mConfig;

    public FileMockNetwork(Context context) {
        this(context, new Config());
    }

    public FileMockNetwork(Context context, Config config) {
        mContext = context;
        mConfig = config;

        // configure the real network for non mocked requests
        if (config.mRealNetwork == null) {
            HttpStack httpStack = (config.mRealNetworkHttpStack == null) ? ConfigUtils.getDefaultHttpStack(mContext) : config.mRealNetworkHttpStack;
            config.mRealNetwork = ConfigUtils.getDefaultNetwork(httpStack);
        }

        if (!mConfig.mBasePath.equals("") && !mConfig.mBasePath.endsWith("/")) {
            mConfig.mBasePath += "/";
        }
    }


    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        if (!shouldMock(request)) {
            return mConfig.mRealNetwork.performRequest(request);
        }

        Mock mock = getMock(request);

        String filePath = mConfig.mBasePath + mock.mFilename;
        BallLogger.d("Mock file path = %s", filePath);

        InputStream is = null;
        byte[] data;
        try {
            is = mContext.getAssets().open(filePath);
            data = IOUtils.toByteArray(is);

        } catch (IOException e) {
            BallLogger.e("Error opening mock file for path %s", filePath);
            throw new NoConnectionError(e);
        } finally {
            IOUtils.closeQuietly(is);
        }

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", mock.mContentType);
        return new NetworkResponse(200, data, headers, false);
    }

    /**
     * Default implementation mocks every request
     *
     * @param request the request that can be mocked
     * @return if the request should be mocked
     */
    protected boolean shouldMock(Request request) {
        return true;
    }

    /**
     * Default implementation respond with file named by the request url last path with ".json" suffix
     * and a content type set to "content/json".
     * <p/>
     * Examples:
     * - GET http://some.url.com/entries                -> get_entries.json
     * - GET http://some.url.com/entries?bla=foobar     -> get_entries.json
     * - POST http://some.url.com/entries               -> post_entries.json
     *
     * @param request the request that will be mocked
     * @return the mock associated to the request
     */
    protected Mock getMock(Request request) {
        if (!request.getUrl().contains("/") || request.getUrl().equals("/")) {
            throw new BallException("Invalid request url for mock, can't determine what is the last path to get the associated mock file : %s", request.getUrl());
        }

        String path = request.getUrl();

        if (path.lastIndexOf("/") == path.length() - 1) {
            path = path.substring(0, path.length() - 2);
        }

        path = FilenameUtils.getBaseName(path);

        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }

        path = RequestUtils.methodToString(request.getMethod()) + "_" + path + ".json";
        path = path.toLowerCase();

        BallLogger.d("Mock request last path %s", path);

        return new Mock(path, "content/json");
    }

    protected static class Mock {
        protected String mFilename;
        protected String mContentType;

        public Mock(String filename, String contentType) {
            mFilename = filename;
            mContentType = contentType;
        }

        public String getFilename() {
            return mFilename;
        }

        public void setFilename(String filename) {
            mFilename = filename;
        }

        public String getContentType() {
            return mContentType;
        }

        public void setContentType(String contentType) {
            mContentType = contentType;
        }
    }

    public static class Config {
        protected Network mRealNetwork;
        protected HttpStack mRealNetworkHttpStack;
        protected String mBasePath = "";
        protected double mRequestDuration = 0.5;

        public Config basePath(String basePath) {
            mBasePath = basePath;
            return this;
        }

        public Config requestDuration(double requestDuration) {
            mRequestDuration = requestDuration;
            return this;
        }

        public Config realNetwork(Network realNetwork) {
            mRealNetwork = realNetwork;
            return this;
        }

        public Config realNetworkHttpStack(HttpStack httpStack) {
            mRealNetworkHttpStack = httpStack;
            return this;
        }
    }
}
