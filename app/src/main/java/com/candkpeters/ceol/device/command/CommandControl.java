package com.candkpeters.ceol.device.command;

import android.util.Log;

import com.candkpeters.ceol.model.PlayStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandControl extends Command {

    private static final String TAG = "CommandControl";
    protected PlayStatusType playStatusType;

    public CommandControl() {
        this( PlayStatusType.Stop);
    }

    public CommandControl(PlayStatusType playStatusType) {
        super();
        this.playStatusType = playStatusType;
    }

    @Override
    protected boolean isSuccessful() {
        switch (ceolDevice.getSIStatus()) {

            case NetServer:
                return ceolDevice.getPlayStatus() == playStatusType;
            case IRadio:
            case AnalogIn:
            case Unknown:
            case CD:
            case Tuner:
            default:
                return true;
        }
    }

    @Override
    public void execute() {
        String commandString = null;

        switch (ceolDevice.getSIStatus()) {

            case Unknown:
                break;
            case CD:
                // TODO
                break;
            case Tuner:
                break;
            case IRadio:
                break;
            case NetServer:
                switch (playStatusType) {
                    case Unknown:
                        break;
                    case Play:
                        commandString = "NS9A";
                        break;
                    case Pause:
                        commandString = "NS9B";
                        break;
                    case Stop:
                        commandString = "NS9C";
                        break;
                }
                break;
            case AnalogIn:
                break;
        }
        ceolCommandManager.sendCommand(commandString);
    }

    @Override
    public String getParameterAsString() {
        return playStatusType.toString();
    }

    @Override
    public void initialize(String valueString) {
        playStatusType = PlayStatusType.valueOf(valueString);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(" + playStatusType+ ")";
    }

}
