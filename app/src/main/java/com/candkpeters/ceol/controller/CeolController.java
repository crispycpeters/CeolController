package com.candkpeters.ceol.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.candkpeters.ceol.device.CeolManager;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.device.command.CommandControlPlayAt;
import com.candkpeters.ceol.device.command.CommandControlStop;
import com.candkpeters.ceol.device.command.CommandControlToggle;
import com.candkpeters.ceol.device.command.CommandCursor;
import com.candkpeters.ceol.device.command.CommandCursorEnter;
import com.candkpeters.ceol.device.command.CommandMasterVolumeDown;
import com.candkpeters.ceol.device.command.CommandMasterVolumeUp;
import com.candkpeters.ceol.device.command.CommandSetPowerToggle;
import com.candkpeters.ceol.device.command.CommandSkipBackward;
import com.candkpeters.ceol.device.command.CommandSkipForward;
import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.ceol.model.OnControlChangedListener;
import com.candkpeters.ceol.model.StreamingStatus;
import com.candkpeters.ceol.service.CeolService;
import com.candkpeters.ceol.service.CeolServiceBinder;
import com.candkpeters.chris.ceol.R;


/**
 * Created by crisp on 07/01/2016.
 */
public class CeolController implements View.OnClickListener {
    private static final String TAG = "CeolController";

//    Prefs prefs;
    Context context;
    CeolService ceolService;
    CeolManager ceolManager;

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
            ceolService = binder.getCeolService();
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
/*
    public void performMacro() {
        ceolManager.execute(new CommandMacro(1));
    }
*/

    public void setViewCommandHandlers(View rootView) {

        setClickHandler( rootView, R.id.powerB, new CommandSetPowerToggle());
        setClickHandler( rootView, R.id.volumeupB, new CommandMasterVolumeUp());
        setClickHandler( rootView, R.id.volumedownB, new CommandMasterVolumeDown());
        setClickHandler( rootView, R.id.skipBackwardsB, new CommandSkipBackward());
        setClickHandler( rootView, R.id.skipForwardsB, new CommandSkipForward());
        setClickHandler( rootView, R.id.playpauseB, new CommandControlToggle());
        setClickHandler( rootView, R.id.stopB, new CommandControlStop());
        setClickHandler( rootView, R.id.navLeftB, new CommandCursor(DirectionType.Left));
        setClickHandler( rootView, R.id.navRightB, new CommandCursor(DirectionType.Right));
        setClickHandler( rootView, R.id.navUpB, new CommandCursor(DirectionType.Up));
        setClickHandler( rootView, R.id.navDownB, new CommandCursor(DirectionType.Down));
        setClickHandler( rootView, R.id.navEnterB, new CommandCursorEnter());

    }

    private void setClickHandler( View rootView, int resID, Command command ) {
        View view;
        if ( (view = (View)rootView.findViewById(resID)) != null ) {
            view.setOnClickListener(this);
            view.setTag(command);
        }
    }

    @Override
    public void onClick(View v) {
        if ( bound && v.getTag() != null && v.getTag() instanceof Command ) {
            Command command = (Command)v.getTag();
//            ceolManager.execute(command);
        }
    }

    public void togglePlaylistItem(AudioStreamItem item, boolean isCurrentTrack) {
        if ( isCurrentTrack && getCeolModel().inputControl.getStreamingStatus() == StreamingStatus.OPENHOME) {
            // Toggle the playstate
            Command command = new CommandControlToggle();
            performCommand(command);
        } else {
            // We want to start openhome with this track
            Command command = new CommandControlPlayAt(item.getId());
            performCommand(command);
        }
    }
}
