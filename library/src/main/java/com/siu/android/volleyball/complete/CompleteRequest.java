package com.siu.android.volleyball.complete;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.siu.android.volleyball.BallRequest;
import com.siu.android.volleyball.BallResponse;
import com.siu.android.volleyball.local.LocalRequestProcessor;
import com.siu.android.volleyball.network.NetworkRequestProcessor;
import com.siu.android.volleyball.response.ResponseListener;

/**
 * Created by lukas on 8/29/13.
 */
public abstract class CompleteRequest<T> extends BallRequest<T> {

    protected ResponseListener mResponseListener;

    public CompleteRequest(int method, String url, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
    }

    protected CompleteRequest(int method, String url, ResponseListener<T> responseListener, Response.ErrorListener errorListener) {
        super(method, url, responseListener, errorListener);
    }


    /* LOCAL */

    @Override
    public boolean shouldProcessLocal() {
        return true;
    }

    @Override
    protected LocalRequestProcessor<T> createLocalRequestProcessor() {
        return new LocalRequestProcessor<T>() {
            @Override
            public T getLocalResponse() {
                return CompleteRequest.this.getLocalResponse();
            }

            @Override
            public void saveLocalResponse(T response) {
                CompleteRequest.this.saveNetworkResponseToLocal(response);
            }
        };
    }

    protected abstract T getLocalResponse();

    public abstract void saveNetworkResponseToLocal(T response);


    /* NETWORK */

    @Override
    public boolean shouldProcessNetwork() {
        return true;
    }

    @Override
    protected NetworkRequestProcessor createNetworkRequestProcessor() {
        return new NetworkRequestProcessor() {
            @Override
            public BallResponse<T> parseNetworkResponse(NetworkResponse networkResponse) {
                return parseBallNetworkResponse(networkResponse);
            }
        };
    }

    protected abstract BallResponse<T> parseBallNetworkResponse(NetworkResponse response);
}
