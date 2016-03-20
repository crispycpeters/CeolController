package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.DirectionType;

/**
 * Created by crisp on 25/01/2016.
 */
public abstract class CommandBaseDirection extends Command {

    private DirectionType directionType;

    public CommandBaseDirection(DirectionType directionType) {
        super();
        this.directionType = directionType;
    }

    public DirectionType getDirectionType() {
        return directionType;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(" + directionType + ")";
    }

/*
    protected Intent fillIntent(Intent intent) {
        return intent.putExtra(CeolService.EXECUTE_COMMAND_VALUE,directionType.toString());
    }
*/

    @Override
    public String getParameterAsString() {
        return directionType.toString();
    }

    @Override
    protected void initialize(String valueString) {
        directionType = DirectionType.valueOf(valueString);
    }
}
