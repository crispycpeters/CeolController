package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.PlayStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandControlToggle extends CommandControl {

    private static final String TAG = "CmdControlToggle";

    public CommandControlToggle() {
        super(PlayStatusType.Stopped);
    }

    @Override
    protected boolean isSuccessful() {
        // Todo need to work out how to do this
        return true;
    }

    @Override
    public void execute() {

        switch (ceolDevice.getSIStatus()) {
            case NotConnected:
                break;
            case CD:
                // TODO
                break;
            case Tuner:
                break;
            case NetServer:
            case IRadio:
            case Ipod:
                playStatusType = toggleCurrentStatus();
                ceolCommandManager.sendCommand(playStatusType==PlayStatusType.Playing?"NS9A":"NS9B");
                break;
            case Spotify:
                ceolCommandManager.sendMediaCommand("PLAY");
                playStatusType = toggleCurrentStatus();
                break;
            case AnalogIn:
                break;
        }
    }

    private PlayStatusType toggleCurrentStatus() {
        PlayStatusType currentPlayStatus;
        switch (ceolDevice.getPlayStatus()) {
            case Playing:
                currentPlayStatus = PlayStatusType.Paused;
                break;
            default:
            case Unknown:
            case Paused:
            case Stopped:
                currentPlayStatus = PlayStatusType.Playing;
                break;
        }
        return currentPlayStatus;
    }

    @Override
    public String getParameterAsString() {
        return playStatusType.toString();
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(" + playStatusType+ ")";
    }

}
