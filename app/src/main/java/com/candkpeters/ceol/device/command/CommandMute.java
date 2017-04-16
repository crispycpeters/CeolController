package com.candkpeters.ceol.device.command;

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
            else return !getValue() && !ceolDevice.isMuted();
        } else {
            return false;
        }
    }

    @Override
    public void execute() {
        ceolManager.sendCommand(getValue() ? "MUON" : "MUOFF");
    }

}
