package com.candkpeters.ceol.device.command;

import android.util.Log;

import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.SIStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandBrowseToRoot extends Command {

    private static final String TAG = "CommandBrowseToRoot";

    public CommandBrowseToRoot() {
        super();
        Log.d(TAG, "constructor: isdone=" + isDone());
    }

    @Override
    protected boolean isSuccessful() {
        Log.d(TAG, "isSuccessful: scrd end=" + ceolDevice.NetServer.getScridValue().endsWith(".1") + " isbrowsing=" + ceolDevice.NetServer.isBrowsing());
        return (
                ceolDevice.getSIStatus() == SIStatusType.NetServer &&
                ceolDevice.NetServer.getScridValue().endsWith(".1") &&
                ceolDevice.NetServer.isBrowsing() );
    }

    private void checkStatus() {
        Log.d(TAG, "checkStatus: isdone=" + isDone());
        if ( ceolDevice.getSIStatus() != SIStatusType.NetServer) {
            setIsDone(true);
        }
        if (!isSuccessful()) {
            new CommandCursorLeft(ceolDevice.NetServer.getScridValue()).execute(ceolManager, new OnCeolStatusChangedListener() {
                @Override
                public void onCeolStatusChanged(CeolDevice ceolDevice) {
                    checkStatus();
                }
            });
        }
    }

    @Override
    public void execute() {
        Log.d(TAG, "execute: isdone=" + isDone());
        checkStatus();
    }

    @Override
    public String getParameterAsString() {
        return "";
    }

    @Override
    public void initialize(String valueString) {
        return;
    }
}
