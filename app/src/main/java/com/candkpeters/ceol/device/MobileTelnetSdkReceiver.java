package com.candkpeters.ceol.device;

import android.util.Log;

import com.candkpeters.ceol.controller.CeolController;

import mobiletelnetsdk.feng.gao.TelnetNotification;

/**
 * Created by chris on 10/11/2015.
 */
public class MobileTelnetSdkReceiver implements TelnetNotification {

    CeolController ceolController;

    public MobileTelnetSdkReceiver(CeolController ceolController) {
        this.ceolController = ceolController;
    }

    @Override
    public void notificationHandler(String s) {
        CeolTelnetParser nse = new CeolTelnetParser();
        nse.setCeolStatus(s);
    }

    @Override
    public void notificationConnectionStatus(int i) {
        Log.d("X","Got connection status " + i);
    }
}

