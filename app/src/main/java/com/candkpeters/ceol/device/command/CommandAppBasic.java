package com.candkpeters.ceol.device.command;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandAppBasic extends CommandBaseApp {

    public CommandAppBasic() {
        super(Action.BASIC);
    }

    @Override
    protected boolean isSuccessful() {
        return true;
    }

    @Override
    protected void execute() {
    }

    @Override
    public String getParameterAsString() {
        return null;
    }

    @Override
    protected void initialize(String valueString) throws InstantiationException {
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

}
