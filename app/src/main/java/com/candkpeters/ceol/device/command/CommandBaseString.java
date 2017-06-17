package com.candkpeters.ceol.device.command;

/**
 * Created by crisp on 25/01/2016.
 */
abstract class CommandBaseString extends Command {
    private String value;

    CommandBaseString(String value) {
        super();
        this.value = value;
    }

    CommandBaseString() {
        this("");
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(" + value + ")";
    }

/*
    protected Intent fillIntent(Intent intent) {
        return intent.putExtra(CeolService.EXECUTE_COMMAND_VALUE,value);
    }
*/

    @Override
    public String getParameterAsString() {
        return getValue();
    }

    @Override
    public void initialize(String valueString) {
        value = valueString;

    }

}
