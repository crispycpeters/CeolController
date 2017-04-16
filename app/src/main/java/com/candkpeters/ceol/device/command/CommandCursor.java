package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.DirectionType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandCursor extends CommandBaseDirection {

    public CommandCursor() {
        this(DirectionType.Down);
    }

    public CommandCursor(DirectionType directionType) {
        super( directionType);
    }

    @Override
    protected boolean isSuccessful() {
        // Todo
        return true;
    }

    @Override
    public void execute() {
        String commandString = null;
        // TODO Do we need to check SI?
        switch ( getDirectionType()) {

            case Plus:
            case Down:
                commandString = "NS91";
                break;
            case Minus:
            case Up:
                commandString = "NS90";
                break;
            case Forward:
            case Right:
                commandString = "NS93";
                break;
            case Backward:
            case Left:
                commandString = "NS92";
                break;
        }
        ceolManager.sendCommand(commandString);
    }


}
