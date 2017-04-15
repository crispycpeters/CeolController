package com.candkpeters.ceol.service;


import android.os.Binder;

import com.candkpeters.ceol.device.CeolCommandManager;

/**
 * Created by crisp on 06/04/2017.
 */

public class CeolServiceBinder extends Binder {

    private CeolService ceolService;

    public CeolServiceBinder( CeolService ceolService) {
        super();
        this.ceolService = ceolService;
    }

    public CeolService getCeolService() {
        return ceolService;
    }
}
