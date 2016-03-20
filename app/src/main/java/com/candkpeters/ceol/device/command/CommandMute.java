package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.CommandType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandMute extends CommandBaseBoolean {

    public CommandMute(boolean onOff) {
        super( onOff);
    }

    @Override
    protected boolean isSuccessful() {
        if ( ceolDevice != null ) {
            if ( getValue() && ceolDevice.isMuted() ) return true;
            else if ( !getValue() && !ceolDevice.isMuted() ) return true;
            else return false;
        } else {
            return false;
        }
    }

    @Override
    public void execute() {
        ceolCommandManager.sendCommand(getValue() ? "MUON" : "MUOFF");
    }

}
