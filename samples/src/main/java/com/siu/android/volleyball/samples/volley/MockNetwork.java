package com.siu.android.volleyball.samples.volley;

import com.android.volley.Request;
import com.siu.android.volleyball.mock.FileMockNetwork;

/**
 * Created by lukas on 8/30/13.
 */
public class MockNetwork extends FileMockNetwork {

    @Override
    protected boolean shouldMock(Request request) {
        return false;
    }

    @Override
    protected Mock respondWithMock(Request request) {
        return null;
    }
}
