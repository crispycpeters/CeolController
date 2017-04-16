package com.candkpeters.ceol.device.command;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandCursorEnter extends Command {

    public CommandCursorEnter() {
        super();
    }

    @Override
    protected boolean isSuccessful() {
        // Todo
        return true;
    }

    @Override
    public void execute() {
        ceolManager.sendCommand("NS94");
    }

    @Override
    public String getParameterAsString() {
        return "";
    }

    @Override
    protected void initialize(String valueString) throws InstantiationException {
        return;
    }

}
