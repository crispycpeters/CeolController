package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.CommandType;

/**
 * Created by crisp on 03/02/2016.
 */
public class CommandDeleteFavorite extends CommandBaseInteger {

    public CommandDeleteFavorite(int value) {
        super ( value);
    }

    @Override
    protected boolean isSuccessful() {
        //Todo
        return false;
    }

    @Override
    public void execute() {

        ceolCommandManager.sendCommand("FVDEL " + String.format("%02d", getValue()));

    }
}
