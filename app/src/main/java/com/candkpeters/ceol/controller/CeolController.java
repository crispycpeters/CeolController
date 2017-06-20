package com.candkpeters.ceol.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.candkpeters.ceol.device.CeolManager;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.device.command.CommandControlToggle;
import com.candkpeters.ceol.device.command.CommandOpenHomeSeekAbsoluteSecond;
import com.candkpeters.ceol.device.command.CommandOpenHomeSeekIndex;
import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.OnControlChangedListener;
import com.candkpeters.ceol.model.StreamingStatus;
import com.candkpeters.ceol.service.CeolService;
import com.candkpeters.ceol.service.CeolServiceBinder;


/**
 * Created by crisp on 07/01/2016.
 */
public class CeolController {
    private static final String TAG = "CeolController";

//    Prefs prefs;
    private Context context;
    private CeolManager ceolManager;

    private boolean bound = false;

    private OnControlChangedListener onControlChangedListener;

    public CeolController(Context context) {
        this.context = context;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.d(TAG, "onServiceConnected: Connected to CeolService");
            CeolServiceBinder binder = (CeolServiceBinder) service;
            CeolService ceolService = binder.getCeolService();
            ceolManager = ceolService.getCeolManager();
            ceolManager.ceolModel.register(onControlChangedListener);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "onServiceDisconnected: Disconnected from CeolService");
            bound = false;
        }
    };

    public boolean isBound() {
        return bound;
    }

    public CeolModel getCeolModel() {
        return ceolManager.ceolModel;
    }

    public CeolManager getCeolManager() {
        return ceolManager;
    }

    public boolean isDebugMode() {
        return ceolManager.isDebugMode();
    }

    public void create() {
        if (!bound) {
            Log.d(TAG, "create: Binding");
            Intent intent = new Intent(context, CeolService.class);
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void stop() {
        Log.d(TAG, "stop: ("+bound+")");
        if ( bound) {
            ceolManager.ceolModel.unregister(onControlChangedListener);
        }
        this.onControlChangedListener = null;
    }

    public void start(OnControlChangedListener onControlChangedListener) {
        Log.d(TAG, "start: ("+bound+")");
        this.onControlChangedListener= onControlChangedListener;
        create();
        if (bound) {
            ceolManager.ceolModel.register(onControlChangedListener);
        }
    }

    public void destroy() {
        stop();
        if (bound) {
            Log.d(TAG, "destroy: Unbinding");
            context.unbindService(serviceConnection);
        }
    }

    public void performCommand( Command command ) {
        if ( command != null && bound) {
            ceolManager.execute(command);
        }
    }

    public boolean isConnected() {
        boolean isConnected = false;
        if ( ceolManager != null ) {
            if ( ceolManager.ceolModel.connectionControl.isConnected()) {
                isConnected = true;
            }
        }
        return isConnected;
    }

    public void restart() {
        if ( ceolManager != null) {
            ceolManager.startGatherers();
        }
    }

    public void togglePlaylistItem(AudioStreamItem item, boolean isCurrentTrack) {
        if ( isCurrentTrack && getCeolModel().inputControl.getStreamingStatus() == StreamingStatus.OPENHOME) {
            // Toggle the playstate
            Command command = new CommandControlToggle();
            performCommand(command);
        } else {
            // We want to start openhome with this track
            Command command = new CommandOpenHomeSeekIndex(item.getId());
            performCommand(command);
        }
    }

    public void setTrackPosition(int newPosition) {
//        int progressSize = (int)(getCeolModel().inputControl.trackControl.getAudioItem().getDuration());

        Command command = new CommandOpenHomeSeekAbsoluteSecond(newPosition);
        performCommand(command);
    }
}
