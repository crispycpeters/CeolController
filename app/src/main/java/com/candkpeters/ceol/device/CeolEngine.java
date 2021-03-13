package com.candkpeters.ceol.device;

import android.content.Context;

import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.view.Prefs;

public abstract class CeolEngine {

    protected final Context context;
    protected final CeolModel ceolModel;

    protected CeolEngine (Context context, CeolModel ceolModel) {
        this.ceolModel = ceolModel;
        this.context = context;
    }


    protected Prefs getPrefs() {
        return new Prefs(context);
    }

    abstract protected void start();

    abstract protected void stop();

    abstract protected void nudge();

    abstract protected void sendCommandStr( String commandStr );
    abstract public void sendCommandSeekTrack(int trackId );
    abstract public void sendCommandSeekAbsoluteSecond( int absoluteSeconds);
}
