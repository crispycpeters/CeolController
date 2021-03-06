package com.candkpeters.ceol.device.command;

import android.util.Log;

import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.ceol.model.SIStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandCursorUp extends CommandCursor {

    private static final String TAG = "CmdCursorUp";
    String currentLevel;

    public CommandCursorUp(String currentLevel) {
        super(DirectionType.Up);
        this.currentLevel= currentLevel;
    }

    public CommandCursorUp() {
        this(null);
    }

    @Override
    protected boolean isSuccessful() {
        if ( currentLevel == null) {
            return true;
        } else {
            Log.d(TAG, "isSuccessful: scrid="+ceolModel.inputControl.navigatorControl.getScridValue() + " currentLevel="+currentLevel );
            boolean result = (
                    ceolModel.inputControl.getSIStatus() == SIStatusType.NetServer &&
                        !ceolModel.inputControl.navigatorControl.getScridValue().equals(currentLevel) &&
                        ceolModel.inputControl.navigatorControl.isBrowsing() );
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
