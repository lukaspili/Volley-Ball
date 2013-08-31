package com.siu.android.volleyball.local;

/**
 * Created by lukas on 8/26/13.
 */
public abstract class LocalRequestProcessor<T> {

    public abstract T getLocalResponse();

    public abstract void saveLocalResponse(T response);

    public abstract void deliverLocalResponse(T response);


}
