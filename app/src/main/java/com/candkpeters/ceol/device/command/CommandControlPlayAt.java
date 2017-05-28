package com.candkpeters.ceol.device.command;

import android.util.Log;

import com.candkpeters.ceol.model.PlayStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandControlPlayAt extends Command {

    private static final String TAG = "CmdControlToggle";
    private int trackId;

    public CommandControlPlayAt(int trackId) {
        super();
        this.trackId = trackId;
    }
    public CommandControlPlayAt() {
        super();
        this.trackId = 0;
    }

    @Override
    protected boolean isSuccessful() {
        // Todo need to work out how to do this
        return true;
    }

    @Override
    public void execute() {

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
                ceolManager.sendOpenHomeSeekIdCommand(trackId);
                break;
            case AnalogIn:
                break;
        }
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
        return String.valueOf(trackId);
    }

    protected void initialize(String valueString) {
        try {
            trackId = Integer.parseInt(valueString);
        } catch ( IllegalArgumentException e ) {
            Log.e(TAG, "initialize: Could not initialize SIStatus with: " + valueString + ": Stack: " + e.getStackTrace() );
        }
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(" + trackId+ ")";
    }

}
