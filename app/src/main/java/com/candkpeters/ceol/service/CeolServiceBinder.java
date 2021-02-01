package com.candkpeters.ceol.service;


import android.os.Binder;

/**
 * Created by crisp on 06/04/2017.
 */

public class CeolServiceBinder extends Binder {

    private final CeolService ceolService;

    public CeolServiceBinder( CeolService ceolService) {
        super();
        this.ceolService = ceolService;
    }

    public CeolService getCeolService() {
        return ceolService;
    }
}
