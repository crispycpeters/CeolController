package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.DirectionType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandMasterVolume extends CommandBaseDirection {

    public CommandMasterVolume(DirectionType directionType) {
        super(directionType);
    }

    public CommandMasterVolume() {
        this(DirectionType.Down);
    }

    @Override
    protected boolean isSuccessful() {
        return true; // TODO
    }

    @Override
    public void execute() {
        ceolCommandManager.sendCommand(getDirectionType().isPositiveDirection ? "MVUP" : "MVDOWN");
    }

}
