package com.siu.android.volleyball.exception;

/**
 * Created by lukas on 8/31/13.
 */
public class BallException extends RuntimeException {

    public BallException() {
    }

    public BallException(String detailMessage) {
        super(detailMessage);
    }

    public BallException(String detailMessage, Object... args) {
        super(String.format(detailMessage, args));
    }



    public BallException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BallException(Throwable throwable) {
        super(throwable);
    }
}
