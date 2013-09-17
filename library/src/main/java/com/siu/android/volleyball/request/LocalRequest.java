package com.siu.android.volleyball.request;

import com.android.volley.Response;
import com.siu.android.volleyball.BallRequest;
import com.siu.android.volleyball.local.LocalRequestProcessor;
import com.siu.android.volleyball.response.ResponseListener;
import com.siu.android.volleyball.response.SingleResponseListener;

/**
 * Created by lukas on 9/17/13.
 */
public abstract class LocalRequest<T> extends BallRequest<T> {

    protected LocalRequest() {
        super(-1, null, null);
    }

    protected LocalRequest(SingleResponseListener<T> responseListener) {
        super(-1, null, responseListener, null);
    }

    @Override
    public boolean shouldProcessLocal() {
        return true;
    }

    @Override
    protected LocalRequestProcessor<T> createLocalRequestProcessor() {
        return new LocalRequestProcessor<T>() {
            @Override
            public T getLocalResponse() {
                return performLocal();
            }

            @Override
            public void saveLocalResponse(T response) {

            }
        };
    }

    public abstract T performLocal();
}
