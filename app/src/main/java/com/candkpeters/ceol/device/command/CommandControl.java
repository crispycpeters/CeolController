package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.PlayStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
class CommandControl extends Command {

    private static final String TAG = "CommandControl";
    PlayStatusType playStatusType;

    public CommandControl() {
        this( PlayStatusType.Stopped);
    }

    CommandControl(PlayStatusType playStatusType) {
        super();
        this.playStatusType = playStatusType;
    }

    @Override
    protected boolean isSuccessful() {
        switch (ceolModel.inputControl.getSIStatus()) {

            case NetServer:
            case OpenHome:
                return ceolModel.inputControl.trackControl.getPlayStatus() == playStatusType;
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

        switch (ceolModel.inputControl.getStreamingStatus()) {

            case CEOL:
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
            case DLNA:
                break;
            case OPENHOME:
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
            case NONE:
                break;
            case SPOTIFY:
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
