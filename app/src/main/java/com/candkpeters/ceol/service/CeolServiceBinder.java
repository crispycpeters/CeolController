package com.candkpeters.ceol.service;


import android.os.Binder;

import com.candkpeters.ceol.device.CeolCommandManager;

/**
 * Created by crisp on 06/04/2017.
 */

public class CeolServiceBinder extends Binder {

    private CeolCommandManager ceolCommandManager;

    public CeolServiceBinder( CeolCommandManager ceolCommandManager) {
        super();
        this.ceolCommandManager = ceolCommandManager;
    }

    public CeolCommandManager getCeolCommandManager() {
        return ceolCommandManager;
    }
}
