package com.candkpeters.ceol.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.candkpeters.ceol.device.CeolManager;
import com.candkpeters.ceol.device.CeolDeviceWebSvcMonitor;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
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
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.ceol.service.CeolService;
import com.candkpeters.ceol.service.CeolServiceBinder;
import com.candkpeters.chris.ceol.R;

//import org.apache.commons.net.telnet.EchoOptionHandler;
//import org.apache.commons.net.telnet.SuppressGAOptionHandler;
//import org.apache.commons.net.telnet.TelnetClient;
//import org.apache.commons.net.telnet.TerminalTypeOptionHandler;


/**
 * Created by crisp on 07/01/2016.
 */
public class CeolController implements View.OnClickListener {
    private static final String TAG = "CeolController";

    CeolDeviceWebSvcMonitor ceolWebService = null;
//    Prefs prefs;
    Context context;
    CeolService ceolService;
    CeolDevice ceolDevice;
    CeolManager ceolManager;
    boolean bound = false;

    OnCeolStatusChangedListener onCeolStatusChangedListener;

    public CeolController( Context context, final OnCeolStatusChangedListener onCeolStatusChangedListener ) {
//        this.prefs = new Prefs(context);
//        String baseurl = prefs.getBaseUrl();

        if ( onCeolStatusChangedListener != null) {
            this.onCeolStatusChangedListener = onCeolStatusChangedListener;
        }
        this.context = context;
        //ceolManager = CeolManager.getInstance();
//        this.ceolManager = ceolManager;
//        ceolDevice = ceolManager.getCeolDevice();
        //ceolManager.initialize(context);//ceolDevice, baseurl, prefs.getMacroNames(), prefs.getMacroValues());
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CeolServiceBinder binder = (CeolServiceBinder) service;
            ceolService = binder.getCeolService();
            ceolManager = ceolService.getCeolManager();
            ceolDevice = ceolManager.getCeolDevice();
            ceolManager.register(onCeolStatusChangedListener);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    public CeolDevice getCeolDevice() {
        return ceolDevice;
    }

    private void stopListening() {
        Log.d(TAG, "stopListening: ("+bound+")");
        if (bound) {
            ceolManager.unregister(onCeolStatusChangedListener);
        }
    }

    private void startListening() {
        Log.d(TAG, "startListening: ("+bound+")");
        if (bound) {
            ceolManager.register(onCeolStatusChangedListener);
        } else {
            Intent intent = new Intent(context, CeolService.class);
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        //ceolManager.register(onCeolStatusChangedListener);
    }
/*

    public void volumeUp() {
        ceolManager.execute(new CommandMasterVolume(DirectionType.Up));
    }

    public void volumeDown() {
        ceolManager.execute(new CommandMasterVolume(DirectionType.Down));
    }

    public void skipBackwards() {
        ceolManager.execute(new CommandSkip(DirectionType.Backward));
    }

    public void skipForwards() {
        ceolManager.execute(new CommandSkip(DirectionType.Forward));
    }
*/

    public void performCommand( Command command ) {
        if ( command != null && bound) {
            ceolManager.execute(command);
        }
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
            ceolManager.execute(command);
        }
    }
}
