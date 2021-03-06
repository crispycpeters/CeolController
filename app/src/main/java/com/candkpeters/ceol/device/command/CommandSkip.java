package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.DirectionType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandSkip extends CommandBaseDirection {

    public CommandSkip(DirectionType directionType) {
        super( directionType);
    }

    @Override
    protected boolean isSuccessful() {
        return true;
    }

    @Override
    public void execute() {
        String commandString = null;

        switch (ceolModel.inputControl.getSIStatus()) {

            case Unknown:
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
                commandString = getDirectionType().isPositiveDirection ? "NS9D" : "NS9E";
                ceolManager.sendCommand(commandString);
                break;
            case AnalogIn:
                break;
            case Spotify:
                commandString = getDirectionType().isPositiveDirection ? "NEXT" : "PREVIOUS";
                ceolManager.sendSpotifyCommand(commandString);
                break;
            case OpenHome:
                commandString = getDirectionType().isPositiveDirection ? "Next" : "Previous";
                ceolManager.sendOpenHomeCommand(commandString);
                break;
        }
    }

}
