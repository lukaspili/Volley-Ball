package com.siu.android.volleyball;

import android.os.SystemClock;

import com.android.volley.VolleyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 8/29/13.
 */
public class BallMarkerLog {
    public static final boolean ENABLED = VolleyLog.DEBUG;

    /** Minimum duration from first marker to last in an marker log to warrant logging. */
    private static final long MIN_DURATION_FOR_LOGGING_MS = 0;

    private static class Marker {
        public final String name;
        public final long thread;
        public final long time;

        public Marker(String name, long thread, long time) {
            this.name = name;
            this.thread = thread;
            this.time = time;
        }
    }

    private final List<Marker> mMarkers = new ArrayList<Marker>();
    private boolean mFinished = false;

    /** Adds a marker to this log with the specified name. */
    public synchronized void add(String name, long threadId) {
        if (mFinished) {
            throw new IllegalStateException("Marker added to finished log");
        }

        mMarkers.add(new Marker(name, threadId, SystemClock.elapsedRealtime()));
    }

    /**
     * Closes the log, dumping it to logcat if the time difference between
     * the first and last markers is greater than {@link #MIN_DURATION_FOR_LOGGING_MS}.
     * @param header Header string to print above the marker log.
     */
    public synchronized void finish(String header) {
        mFinished = true;

        long duration = getTotalDuration();
        if (duration <= MIN_DURATION_FOR_LOGGING_MS) {
            return;
        }

        long prevTime = mMarkers.get(0).time;
        VolleyLog.d("(%-4d ms) %s", duration, header);
        for (Marker marker : mMarkers) {
            long thisTime = marker.time;
            VolleyLog.d("(+%-4d) [%2d] %s", (thisTime - prevTime), marker.thread, marker.name);
            prevTime = thisTime;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        // Catch requests that have been collected (and hence end-of-lifed)
        // but had no debugging output printed for them.
        if (!mFinished) {
            finish("Request on the loose");
            VolleyLog.e("Marker log finalized without finish() - uncaught exit point for request");
        }
    }

    /** Returns the time difference between the first and last events in this log. */
    private long getTotalDuration() {
        if (mMarkers.size() == 0) {
            return 0;
        }

        long first = mMarkers.get(0).time;
        long last = mMarkers.get(mMarkers.size() - 1).time;
        return last - first;
    }
}
