package com.candkpeters.ceol.device.command;

import android.util.Log;

import com.candkpeters.ceol.model.PlayStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandOpenHomeSeekAbsoluteSecond extends Command {

    private static final String TAG = "CommandAbsSeconds";
    private int absoluteSeconds;

    public CommandOpenHomeSeekAbsoluteSecond(int absoluteSeconds) {
        super();
        this.absoluteSeconds = absoluteSeconds;
    }
    public CommandOpenHomeSeekAbsoluteSecond() {
        super();
        this.absoluteSeconds = 0;
    }

    @Override
    protected boolean isSuccessful() {
        // Todo need to work out how to do this
        return true;
    }

    @Override
    public void execute() {

        ceolManager.sendOpenHomeSeekAbsoluteSecond(absoluteSeconds);
/*
        switch (ceolModel.inputControl.getSIStatus()) {
            case Unknown:
                break;
            case CD:
                // TODO
                break;
            case Tuner:
                break;
            case NetServer:
            case IRadio:
            case Ipod:
            case Spotify:
                break;
            case OpenHome:
                break;
            case AnalogIn:
                break;
        }
*/
    }

    private PlayStatusType toggleCurrentStatus() {
        PlayStatusType currentPlayStatus;
        switch (ceolModel.inputControl.trackControl.getPlayStatus()) {
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
        return String.valueOf(absoluteSeconds);
    }

    protected void initialize(String valueString) {
        try {
            absoluteSeconds = Integer.parseInt(valueString);
        } catch ( IllegalArgumentException e ) {
            Log.e(TAG, "initialize: Could not initialize command with: " + valueString + ": Stack: " + e.getStackTrace() );
        }
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(" + absoluteSeconds+ ")";
    }

}
