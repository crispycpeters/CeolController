package com.candkpeters.ceol.device.command;

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

        ceolManager.sendCommand("FVDEL " + String.format("%02d", getValue()));

    }
}
