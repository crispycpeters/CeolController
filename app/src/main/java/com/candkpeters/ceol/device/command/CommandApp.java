package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.PlayStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandApp extends Command {

    public CommandApp() {
        super();
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
