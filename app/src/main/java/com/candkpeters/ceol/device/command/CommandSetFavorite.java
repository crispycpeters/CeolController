package com.candkpeters.ceol.device.command;

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
