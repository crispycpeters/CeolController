package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.ceol.model.SIStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandBrowseToRoot extends Command {

    private static final String TAG = "CommandBrowseToRoot";

    @Override
    protected boolean isSuccessful() {
        return (
                ceolDevice.getSIStatus() == SIStatusType.NetServer &&
                ceolDevice.NetServer.getScridValue().endsWith(".1") &&
                ceolDevice.NetServer.isBrowsing() );
    }

    @Override
    protected void onCeolStatusChangedListener() {
        checkStatus();
    };

    private void checkStatus() {
        if ( ceolDevice.getSIStatus() != SIStatusType.NetServer) {
            setIsDone(true);
        }
        if (!isSuccessful()) {
            new CommandCursor(DirectionType.Left).execute(ceolCommandManager);
        }
    }

    @Override
    public void execute() {
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
