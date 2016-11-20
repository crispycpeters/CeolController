package com.candkpeters.ceol.device.command;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandAppInfo extends CommandBaseApp {

    public CommandAppInfo() {
        super(Action.INFO);
    }

    @Override
    protected boolean isSuccessful() {
        return true;
    }

    @Override
    protected void execute() {
        return;
    }

    @Override
    public String getParameterAsString() {
        return null;
    }

    @Override
    protected void initialize(String valueString) throws InstantiationException {
        return;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

}
