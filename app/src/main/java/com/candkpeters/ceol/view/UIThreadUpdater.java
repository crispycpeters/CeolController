package com.candkpeters.ceol.view;

import android.os.Handler;
import android.os.Looper;

/**
 * A class used to perform periodical updates,
 * specified inside a runnable object. An update interval
 * may be specified (otherwise, the class will perform the
 * update every 2 seconds).
 *
 * @author Carlos Simões
 */
public class UIThreadUpdater {
    // Create a Handler that uses the Main Looper to run in
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Runnable repeatedRunnable;
//    private Runnable oneoffRunnable;
    private int repeatRateMsecs;
    private long lastUpdate = System.currentTimeMillis();
//    private boolean isRunning = false;

    /**
     * Creates an UIUpdater object, that can be used to
     * perform UIUpdates on a specified time interval.
     *
     * @param uiUpdater A runnable containing the update routine.
     * @param repeatRateMsecs
     * @param isKeepMonitoring
     */
    /**
     * The same as the default constructor, but specifying the
     * intended update interval.
     *  @param uiUpdater A runnable containing the update routine.
     * @param repeatRateMsecs  The interval over which the routine
     */
    public UIThreadUpdater(final Runnable uiUpdater, int repeatRateMsecs){
        this.repeatRateMsecs = repeatRateMsecs;

        repeatedRunnable = new Runnable() {
            @Override
            public void run() {
                lastUpdate = System.currentTimeMillis();
                // Run the passed runnable
                uiUpdater.run();
                // Re-run it after the update interval
                // Will be done within monitor
//                mHandler.postDelayed(this, UIThreadUpdater.this.repeatRateMsecs);
            }
        };
    }

    public synchronized void next() {
        mHandler.removeCallbacks(repeatedRunnable);
        // Could make this calculate time when last update was initiated.
        mHandler.postDelayed(repeatedRunnable, repeatRateMsecs);
    }

    public synchronized void next(int repeatRateMsecs) {
        this.repeatRateMsecs = repeatRateMsecs;
        mHandler.removeCallbacks(repeatedRunnable);
        // Could make this calculate time when last update was initiated.
        mHandler.postDelayed(repeatedRunnable, repeatRateMsecs);
    }

    /**
     * Starts the periodical update routine (repeatedRunnable
     * adds the callback to the handler).
     */
    public synchronized void startUpdates(){
        mHandler.removeCallbacks(repeatedRunnable);
        mHandler.post(repeatedRunnable);
    }

    public synchronized void fireOnce(){
        mHandler.removeCallbacks(repeatedRunnable);
        mHandler.post(repeatedRunnable);
    }

    public synchronized void fireOnce( int msecWait ){
        mHandler.removeCallbacks(repeatedRunnable);
        mHandler.postDelayed(repeatedRunnable,msecWait);
    }

    /**
     * Stops the periodical update routine from running,
     * by removing the callback.
     */
    public synchronized void stopUpdates(){
        mHandler.removeCallbacks(repeatedRunnable);
    }

//    public synchronized boolean isRunning() {
//        return isRunning;
//    }
}