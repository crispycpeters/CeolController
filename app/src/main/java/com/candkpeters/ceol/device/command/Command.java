package com.candkpeters.ceol.device.command;

import android.util.Log;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.model.CeolDevice;

/**
 * Created by crisp on 22/01/2016.
 */
public abstract class Command {

    private static final String TAG = "Command";
    //private final CommandType type;
    protected CeolCommandManager ceolCommandManager;
    protected CeolDevice ceolDevice;
    protected int maxExecutionTimeMsecs = 30000;
    private OnCeolStatusChangedListener onCeolStatusChangedListener;
    private OnCeolStatusChangedListener onDoneCeolStatusChangedListener;
    private long commandStartTime;
    private boolean isDone = false;

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
        ceolCommandManager.unregister(onCeolStatusChangedListener);
        if ( onDoneCeolStatusChangedListener != null) {
            onDoneCeolStatusChangedListener.onCeolStatusChanged(ceolDevice);
        }
    }

    protected void onCeolStatusChangedListener() {
    }

    protected abstract boolean isSuccessful( );

    public boolean isSuccessful(CeolCommandManager ceolCommandManager) {
        setIsDone(false);
        this.ceolCommandManager = ceolCommandManager;
        this.ceolDevice = ceolCommandManager.getCeolDevice();
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

    public void execute( CeolCommandManager ceolCommandManager) {
        execute(ceolCommandManager,null);
    }

    public void execute( CeolCommandManager ceolCommandManager, OnCeolStatusChangedListener onDoneCeolStatusChangedListener) {

        setIsDone(false);
        this.onDoneCeolStatusChangedListener = onDoneCeolStatusChangedListener;
        commandStartTime = System.currentTimeMillis();

        this.ceolCommandManager = ceolCommandManager;
        this.ceolDevice = ceolCommandManager.getCeolDevice();

        preExecute();
        onCeolStatusChangedListener = new OnCeolStatusChangedListener() {
            @Override
            public void onCeolStatusChanged(CeolDevice ceolDevice) {
                checkOverallStatus();
            }
        };
        ceolCommandManager.register(onCeolStatusChangedListener);
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
