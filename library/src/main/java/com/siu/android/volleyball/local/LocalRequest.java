package com.siu.android.volleyball.local;

import com.android.volley.Response;
import com.siu.android.volleyball.BallRequest;
import com.siu.android.volleyball.BallResponse;

/**
 * Created by lukas on 8/29/13.
 */
public abstract class LocalRequest<T> extends BallRequest<T> {

    public LocalRequest(int method, String url, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
    }

    public LocalRequest(int method, String url, BallResponse.ListenerWithLocalProcessing<T> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
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
                return LocalRequest.this.getLocalResponseContent();
            }

            @Override
            public void saveLocalResponse(T response) {
                LocalRequest.this.saveLocalResponse(response);
            }

            @Override
            public void deliverLocalResponse(T response) {
                LocalRequest.this.deliverLocalResponse(response);
            }
        };
    }

    protected abstract T getLocalResponseContent();

    public abstract void saveLocalResponse(T response);

    private void deliverLocalResponse(T response) {
        if (mListenerWithLocalProcessing == null) {
            throw new RuntimeException("Local request listener is null, you need to supply one or override this method");
        }

        mListenerWithLocalProcessing.onLocalResponse(response);
    }
}
