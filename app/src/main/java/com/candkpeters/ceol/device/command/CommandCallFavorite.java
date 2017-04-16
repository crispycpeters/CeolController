package com.candkpeters.ceol.device.command;

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

        ceolManager.sendCommand("FV " + String.format("%02d", getValue()));

    }
}
