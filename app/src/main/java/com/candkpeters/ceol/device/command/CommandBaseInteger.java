package com.candkpeters.ceol.device.command;

/**
 * Created by crisp on 25/01/2016.
 */
public abstract class CommandBaseInteger extends Command {

    private int value;

    CommandBaseInteger(int value) {
        super();
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(" + value + ")";
    }

/*
    protected Intent fillIntent(Intent intent) {
        return intent.putExtra(CeolService.EXECUTE_COMMAND_VALUE,Integer.toString(value));
    }
*/

    @Override
    public String getParameterAsString() {
        return Integer.toString(value);
    }

    @Override
    protected void initialize(String valueString) {
        value = Integer.valueOf(valueString);
    }

}
