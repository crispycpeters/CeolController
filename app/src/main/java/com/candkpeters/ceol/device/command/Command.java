package com.candkpeters.ceol.device.command;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.view.CeolService;

import java.util.List;

/**
 * Created by crisp on 22/01/2016.
 */
public abstract class Command {

    private static final String TAG = "Command";
    //private final CommandType type;
    protected CeolCommandManager ceolCommandManager;
    protected CeolDevice ceolDevice;
    protected int maxExecutionTimeMsecs = 30000;
    OnCeolStatusChangedListener onCeolStatusChangedListener;
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
            ceolCommandManager.unregister(onCeolStatusChangedListener);
            return;
        }
        if ( isDone() ) {
            Log.d(TAG, "checkOverallStatus: We're done for " + this.toString());
            ceolCommandManager.unregister(onCeolStatusChangedListener);
            return;
        }
        if ( System.currentTimeMillis() - commandStartTime >= maxExecutionTimeMsecs  ) {
            Log.e(TAG, "checkOverallStatus: Timeout for " + this.toString());
            ceolCommandManager.unregister(onCeolStatusChangedListener);
            return;
        }
     }

    protected void onCeolStatusChangedListener() {
        return;
    };

    //public CommandType getType() {
    //    return type;
    //}

    protected abstract boolean isSuccessful( );

    public final boolean isDone() {
        return isDone;
    };

    protected void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    protected void preExecute() {
        return;
    };

    protected abstract void execute();

    public void execute( CeolCommandManager ceolCommandManager) {

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
