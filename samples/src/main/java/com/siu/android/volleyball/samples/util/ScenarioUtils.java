package com.siu.android.volleyball.samples.util;

/**
 * Created by lukas on 9/3/13.
 */
public class ScenarioUtils {

    public static final void waitSeveralSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
