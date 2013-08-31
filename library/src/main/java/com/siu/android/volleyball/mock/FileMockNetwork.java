package com.siu.android.volleyball.mock;

import android.content.Context;

import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.siu.android.volleyball.util.BallLogger;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lukas on 8/29/13.
 */
public class FileMockNetwork implements Network {

    private Context mContext;
    private String basePath = "";
    private double mRequestDuration = 0;


    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {

        String lastPath = request.getUrl().substring(request.getUrl().lastIndexOf("/"));
        if (lastPath.contains("?")) {
            lastPath = lastPath.substring(0, lastPath.indexOf("?"));
        }

        BallLogger.d("last path %s", lastPath);

        String filePath = basePath + lastPath;
        InputStream is = null;
        try {
            is = mContext.getAssets().open(filePath);
        } catch (IOException e) {
            BallLogger.e("Error opening mock file for path %s", filePath);
        } finally {
            IOUtils.closeQuietly(is);
        }


//        NetworkResponse networkResponse = new NetworkResponse()

        return null;
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

    protected Mock respondWithMock(Request request) {
        return null;
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
}
