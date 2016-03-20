package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.CommandType;

/**
 * Created by crisp on 03/02/2016.
 */
public class CommandSetFavorite extends CommandBaseInteger {

    public CommandSetFavorite( int value ) {
        super ( value);
    }

    @Override
    protected boolean isSuccessful() {
        return false;
    }

    @Override
    public void execute() {

        ceolCommandManager.sendCommand("FVMEM " + String.format("%02d", getValue()));

    }
}
