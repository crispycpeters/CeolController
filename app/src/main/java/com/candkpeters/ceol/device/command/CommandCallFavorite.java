package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.CommandType;

/**
 * Created by crisp on 03/02/2016.
 */
public class CommandCallFavorite extends CommandBaseInteger {

    public CommandCallFavorite(int value) {
        super ( value);
    }

    @Override
    protected boolean isSuccessful() {
        return true;
    }

    @Override
    public void execute() {

        ceolCommandManager.sendCommand("FV " + String.format("%02d", getValue()));

    }
}
