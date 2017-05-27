package com.candkpeters.ceol.device.command;

import android.util.Log;

import com.candkpeters.ceol.device.CeolManager;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.model.ObservedControlType;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.control.ControlBase;
import com.candkpeters.ceol.model.OnControlChangedListener;

/**
 * Created by crisp on 22/01/2016.
 */
public abstract class Command {

    private static final String TAG = "Command";
    //private final CommandType type;
    protected CeolManager ceolManager;
    protected int maxExecutionTimeMsecs = 30000;
//    private OnCeolStatusChangedListener onCeolStatusChangedListener;
    private OnControlChangedListener onControlChangedListener;
    private OnCeolStatusChangedListener onDoneCeolStatusChangedListener;
    private long commandStartTime;
    private boolean isDone = false;
    protected CeolModel ceolModel;

    public Command() {
        //this.type = type;
    }


    private void checkOverallStatus() {
        if (isSuccessful()) {
            Log.d(TAG, "checkOverallStatus: Success for " + this.toString() + ". Set isDone");
            setIsDone(true);
        }
        onCeolStatusChangedListener();
        if ( isSuccessful() ) {
            Log.d(TAG, "checkOverallStatus: Success for " + this.toString());
            finishUp();
            return;
        }
        if ( isDone() ) {
            Log.d(TAG, "checkOverallStatus: We're done for " + this.toString());
            finishUp();
            return;
        }
        if ( System.currentTimeMillis() - commandStartTime >= maxExecutionTimeMsecs  ) {
            Log.e(TAG, "checkOverallStatus: Timeout for " + this.toString());
            setIsDone(true);
            finishUp();
            return;
        }
    }

    private void finishUp() {
        ceolManager.unregister(onControlChangedListener);
        if ( onDoneCeolStatusChangedListener != null) {
            onDoneCeolStatusChangedListener.onCeolStatusChanged();
        }
    }

    protected void onCeolStatusChangedListener() {
    }

    protected abstract boolean isSuccessful( );

    public boolean isSuccessful(CeolManager ceolManager) {
        setIsDone(false);
        this.ceolManager = ceolManager;
        this.ceolModel = ceolManager.ceolModel;
        boolean result = isSuccessful();
        if (result) {
            setIsDone(true);
        }
        return result;
    }

    public final boolean isDone() {
        return isDone;
    }

    protected void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    protected void preExecute() {
    }

    protected abstract void execute();

    public void execute(CeolManager ceolManager) {
        execute(ceolManager,null);
    }

    public void execute(CeolManager ceolManager, OnCeolStatusChangedListener onDoneCeolStatusChangedListener) {

        setIsDone(false);
        this.onDoneCeolStatusChangedListener = onDoneCeolStatusChangedListener;
        commandStartTime = System.currentTimeMillis();

        this.ceolManager = ceolManager;
        this.ceolModel = ceolManager.ceolModel;

        preExecute();
        onControlChangedListener = new OnControlChangedListener() {
/*
            @Override
            public void onAudioControlChanged(CeolModel ceolModel, AudioControl audioControl) {
                checkOverallStatus();
            }

            @Override
            public void onConnectionControlChanged(CeolModel ceolModel, ConnectionControl connectionControl) {
                checkOverallStatus();
            }

            @Override
            public void onCeolNavigatorControlChanged(CeolModel ceolModel, CeolNavigatorControl ceolNavigatorControl) {
                checkOverallStatus();
            }

            @Override
            public void onInputControlChanged(CeolModel ceolModel, InputControl inputControl) {
                checkOverallStatus();
            }

            @Override
            public void onPowerControlChanged(CeolModel ceolModel, PowerControl powerControl) {
                checkOverallStatus();
            }

            @Override
            public void onTrackControlChanged(CeolModel ceolModel, TrackControl trackControl) {
                checkOverallStatus();
            }

            @Override
            public void onPlaylistControlChanged(CeolModel ceolModel, PlaylistControlBase playlistControlBase) {

            }
*/

            @Override
            public void onControlChanged(CeolModel ceolModel, ObservedControlType observedControlType, ControlBase controlBase) {
                checkOverallStatus();
            }
        };
        ceolManager.register(onControlChangedListener);
        execute();
    }

    //    protected abstract Intent fillIntent( Intent intent);
    public abstract String getParameterAsString();

    public static Command newInstance(String className, String parameterString) {
        Command command = null;
        Log.d(TAG, "newInstance: Have Command name: " + className);
        if ( className != null ) {
            try {
                Class commandClass = Class.forName("com.candkpeters.ceol.device.command." + className);
                command = (Command)commandClass.newInstance();
                if ( parameterString != null ) {
                    command.initialize(parameterString);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (InstantiationException e) {
                e.printStackTrace();
                return null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e(TAG, "newInstance: className is null");
        }
        return command;
    }

    protected abstract void initialize(String valueString) throws InstantiationException;

    public String toString() {
        return this.getClass().getSimpleName();
    }
}
