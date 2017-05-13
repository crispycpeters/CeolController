package com.candkpeters.ceol.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.candkpeters.ceol.device.CeolDeviceWebSvcMonitor;
import com.candkpeters.ceol.device.CeolManager2;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.device.command.CommandControlStop;
import com.candkpeters.ceol.device.command.CommandControlToggle;
import com.candkpeters.ceol.device.command.CommandCursor;
import com.candkpeters.ceol.device.command.CommandCursorEnter;
import com.candkpeters.ceol.device.command.CommandMasterVolumeDown;
import com.candkpeters.ceol.device.command.CommandMasterVolumeUp;
import com.candkpeters.ceol.device.command.CommandSetPowerToggle;
import com.candkpeters.ceol.device.command.CommandSkipBackward;
import com.candkpeters.ceol.device.command.CommandSkipForward;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.ceol.model.OnControlChangedListener;
import com.candkpeters.ceol.service.CeolService;
import com.candkpeters.ceol.service.CeolServiceBinder;
import com.candkpeters.chris.ceol.R;


/**
 * Created by crisp on 07/01/2016.
 */
public class CeolController2 implements View.OnClickListener {
    private static final String TAG = "CeolController2";

    CeolDeviceWebSvcMonitor ceolWebService = null;
//    Prefs prefs;
    Context context;
    CeolService ceolService;
    CeolManager2 ceolManager2;

    private boolean bound = false;

    OnControlChangedListener onControlChangedListener;

    public CeolController2(Context context, final OnControlChangedListener onControlChangedListener) {
        if ( onControlChangedListener!= null) {
            this.onControlChangedListener= onControlChangedListener;
        }
        this.context = context;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CeolServiceBinder binder = (CeolServiceBinder) service;
            ceolService = binder.getCeolService();
            ceolManager2 = ceolService.getCeolManager();
            ceolManager2.ceolModel.register(onControlChangedListener);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    public CeolModel getCeolModel() {
        return ceolManager2.ceolModel;
    }

    private void stopListening() {
        Log.d(TAG, "stopListening: ("+bound+")");
        if (bound) {
            ceolManager2.ceolModel.unregister(onControlChangedListener);
        }
    }

    private void startListening() {
        Log.d(TAG, "startListening: ("+bound+")");
        if (bound) {
            ceolManager2.ceolModel.register(onControlChangedListener);
        } else {
            Intent intent = new Intent(context, CeolService.class);
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }


    public void performCommand( Command command ) {
        if ( command != null && bound) {
            ceolManager2.execute(command);
        }
    }

    public boolean isConnected() {
        boolean isConnected = false;
        if ( ceolManager2 != null ) {
            if ( ceolManager2.ceolModel.connectionControl.isConnected()) {
                isConnected = true;
            }
        }
        return isConnected;
    }

/*
    public void performMacro() {
        ceolManager.execute(new CommandMacro(1));
    }
*/

    public void activityOnStop() {
        stopListening();
    }

    public void activityOnStart() {
        startListening();
    }

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
}
