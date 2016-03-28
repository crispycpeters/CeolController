package com.candkpeters.ceol.device.command;

import android.util.Log;

import com.candkpeters.ceol.model.PlayStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandControlToggle extends CommandControl {

    private static final String TAG = "CmdControlToggle";
    private PlayStatusType playStatusType;

    public CommandControlToggle() {
        super( PlayStatusType.Stop);
    }

    @Override
    protected boolean isSuccessful() {
        // Todo need to work out how to do this
        return true;
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
                playStatusType = toggleCurrentStatus();
                break;
            case AnalogIn:
                break;
        }
        ceolCommandManager.sendCommand(commandString);
    }

    private PlayStatusType toggleCurrentStatus() {
        PlayStatusType currentPlayStatus;
        switch (ceolDevice.getPlayStatus()) {
            case Play:
                currentPlayStatus = PlayStatusType.Pause;
                break;
            default:
            case Unknown:
            case Pause:
            case Stop:
                currentPlayStatus = PlayStatusType.Play;
                break;
        }
        playStatusType = currentPlayStatus;
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
