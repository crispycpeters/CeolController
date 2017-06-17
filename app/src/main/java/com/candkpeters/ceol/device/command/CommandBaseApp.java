package com.candkpeters.ceol.device.command;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandBaseApp extends Command {

    public enum Action {
        BASIC,
        INFO,
        SELECTSI
    }

    private Action action = Action.BASIC;

    CommandBaseApp(Action action) {
        super();
        this.action = action;
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

    public String getAction() {
        return action.name();
    }

}
