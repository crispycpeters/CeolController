package com.candkpeters.ceol.device;

import mobiletelnetsdk.feng.gao.TelnetAPIs;

/**
 * Created by crisp on 29/12/2015.
 */
public class MobileTelnetSdkPumpRunnable implements Runnable {

    @Override
    public void run() {
        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        TelnetAPIs.TelnetPumpMessage();
    }
}
