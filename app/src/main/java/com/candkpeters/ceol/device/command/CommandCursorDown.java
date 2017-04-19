package com.candkpeters.ceol.device.command;

import android.util.Log;

import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.ceol.model.SIStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandCursorDown extends CommandCursor {

    private static final String TAG = "CmdCursorDown";
    String currentLevel;

    public CommandCursorDown(String currentLevel) {
        super(DirectionType.Down);
        this.currentLevel= currentLevel;
    }

    public CommandCursorDown() {
        this(null);
    }

    @Override
    protected boolean isSuccessful() {
        if ( currentLevel == null) {
            return true;
        } else {
            Log.d(TAG, "isSuccessful: scrid="+ceolDevice.CeolNetServer.getScridValue() + " currentLevel="+currentLevel );
            boolean result = (
                    ceolDevice.getSIStatus() == SIStatusType.NetServer &&
                    !ceolDevice.CeolNetServer.getScridValue().equals(currentLevel) &&
                    ceolDevice.CeolNetServer.isBrowsing() );
            return result;
        }
    }

    @Override
    public String getParameterAsString() {
        return currentLevel;
    }

    @Override
    protected void initialize(String valueString) {
        currentLevel= valueString;
    }

}
