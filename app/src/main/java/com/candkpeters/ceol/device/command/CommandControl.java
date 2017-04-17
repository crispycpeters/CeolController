package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.PlayStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandControl extends Command {

    private static final String TAG = "CommandControl";
    protected PlayStatusType playStatusType;

    public CommandControl() {
        this( PlayStatusType.Stopped);
    }

    public CommandControl(PlayStatusType playStatusType) {
        super();
        this.playStatusType = playStatusType;
    }

    @Override
    protected boolean isSuccessful() {
        switch (ceolDevice.getSIStatus()) {

            case NetServer:
            case OpenHome:
                return ceolDevice.getPlayStatus() == playStatusType;
            case IRadio:
            case AnalogIn:
            case NotConnected:
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

            case NotConnected:
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
                    case Playing:
                        commandString = "NS9A";
                        break;
                    case Paused:
                        commandString = "NS9B";
                        break;
                    case Stopped:
                        commandString = "NS9C";
                        break;
                }
                ceolManager.sendCommand(commandString);
                break;
            case AnalogIn:
                break;
            case Spotify:
                switch (playStatusType) {
                    case Unknown:
                        break;
                    case Playing:
                        commandString = "PLAY";
                        break;
                    case Paused:
                    case Stopped:
                        commandString = "PAUSE";
                        break;
                }
                ceolManager.sendSpotifyCommand(commandString);
                break;
            case OpenHome:
                switch (playStatusType) {
                    case Unknown:
                        break;
                    case Playing:
                        commandString = "Play";
                        break;
                    case Paused:
                        commandString = "Pause";
                        break;
                    case Stopped:
                        commandString = "Stop";
                        break;
                }
                ceolManager.sendOpenHomeCommand(commandString);
                break;
        }
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
