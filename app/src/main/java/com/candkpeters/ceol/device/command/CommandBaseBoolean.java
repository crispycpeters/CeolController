package com.candkpeters.ceol.device.command;

/**
 * Created by crisp on 25/01/2016.
 */
public abstract class CommandBaseBoolean extends Command {
    private boolean value;

    public CommandBaseBoolean(boolean value) {
        super();
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(" + value + ")";
    }

/*
    protected Intent fillIntent(Intent intent) {
        return intent.putExtra(CeolService_Old.EXECUTE_COMMAND_VALUE,Boolean.toString(value));
    }
*/

    @Override
    public String getParameterAsString() {
        return Boolean.toString(value);
    }

    @Override
    protected void initialize(String valueString) throws InstantiationException {
        if ( valueString != null && (valueString.equalsIgnoreCase("true") || valueString.equalsIgnoreCase("false"))) {
            value = Boolean.valueOf(valueString);
        } else {
            throw new InstantiationException("Boolean not obtainable from: " + valueString);
        }
    }

}
