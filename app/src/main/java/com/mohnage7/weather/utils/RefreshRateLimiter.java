package com.mohnage7.weather.utils;

import android.os.SystemClock;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * this class adds the loaded data key to the map and check later
 * to determine if we need to refresh data or not.
 * key in @shouldFetch method is the key of the data that needs to be loaded.
 */
public class RefreshRateLimiter<KEY> {
    private long timeout;
    private HashMap<KEY, Long> timestamps;

    public RefreshRateLimiter(TimeUnit timeUnit, long timeout) {
        this.timeout = timeUnit.toMillis(timeout);
        timestamps = new HashMap<>();
    }

    public synchronized boolean shouldFetch(KEY key) {
        Long lastFetched = timestamps.get(key);
        long now = SystemClock.uptimeMillis();
        // if there's no data added before. add it and return true
        if (lastFetched == null) {
            timestamps.put(key, now);
            return true;
        }
        if (now - lastFetched > timeout) {
            timestamps.put(key, now);
            return true;
        }
        return false;
    }

    public synchronized void remove(KEY key) {
        timestamps.remove(key);
    }
}
