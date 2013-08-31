package com.siu.android.volleyball.util;

import com.android.volley.Request;

/**
 * Created by lukas on 8/31/13.
 */
public class RequestUtils {

    /**
     * Get the equivalent string of the int request http method
     *
     * @param method the method as int constant
     * @return the method as string
     * @throws IllegalArgumentException if no match is found between the int constant and the equivalent string
     */
    public static final String methodToString(int method) throws IllegalArgumentException {
        switch (method) {
            case Request.Method.GET:
                return "GET";
            case Request.Method.POST:
                return "POST";
            case Request.Method.PUT:
                return "PUT";
            case Request.Method.DELETE:
                return "DELETE";
        }

        throw new IllegalArgumentException("Unkown method for int constant " + method);
    }
}
