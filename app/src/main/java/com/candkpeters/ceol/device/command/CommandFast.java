package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.CommandType;
import com.candkpeters.ceol.model.DirectionType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandFast extends CommandBaseDirection {

    public CommandFast(DirectionType directionType) {
        super( directionType);
    }

    @Override
    protected boolean isSuccessful() {
        return true;
    }

    @Override
    public void execute() {
        String commandString = null;
        switch (ceolDevice.getSIStatus()) {

            case NotConnected:
                break;
            case CD:
                // TODO
                break;
            case Tuner:
                // TODO

                break;
            case IRadio:
                break;
            case NetServer:
                commandString = getDirectionType().isPositiveDirection ? "NS9F" : "NS9G";
                break;
            case AnalogIn:
                break;
        }
        ceolCommandManager.sendCommand(commandString);
    }

}
