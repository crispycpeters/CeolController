package com.candkpeters.ceol.controller;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.device.CeolDeviceWebSvcMonitor;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.device.command.CommandControlStop;
import com.candkpeters.ceol.device.command.CommandControlToggle;
import com.candkpeters.ceol.device.command.CommandCursor;
import com.candkpeters.ceol.device.command.CommandCursorEnter;
import com.candkpeters.ceol.device.command.CommandFastBackward;
import com.candkpeters.ceol.device.command.CommandFastForward;
import com.candkpeters.ceol.device.command.CommandMacro;
import com.candkpeters.ceol.device.command.CommandMasterVolume;
import com.candkpeters.ceol.device.command.CommandMasterVolumeDown;
import com.candkpeters.ceol.device.command.CommandMasterVolumeUp;
import com.candkpeters.ceol.device.command.CommandSetPowerToggle;
import com.candkpeters.ceol.device.command.CommandSetSI;
import com.candkpeters.ceol.device.command.CommandSkip;
import com.candkpeters.ceol.device.command.CommandSkipBackward;
import com.candkpeters.ceol.device.command.CommandSkipForward;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.ceol.model.SIStatusType;
import com.candkpeters.ceol.view.Prefs;
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
    Prefs prefs;
    CeolDevice ceolDevice;
    CeolCommandManager ceolCommandManager;

    OnCeolStatusChangedListener onCeolStatusChangedListener;

    public CeolController( Context context, final OnCeolStatusChangedListener onCeolStatusChangedListener ) {
//        this.prefs = new Prefs(context);
//        String baseurl = prefs.getBaseUrl();

        this.onCeolStatusChangedListener = onCeolStatusChangedListener;
        ceolCommandManager = CeolCommandManager.getInstance();
        ceolDevice = ceolCommandManager.getCeolDevice();
        ceolCommandManager.initialize(context);//ceolDevice, baseurl, prefs.getMacroNames(), prefs.getMacroValues());
    }

    private void stopListening() {
        Log.d(TAG, "stopListening: ");
        ceolCommandManager.unregister(onCeolStatusChangedListener);
    }

    private void startListening() {
        Log.d(TAG, "startListening: ");
        ceolCommandManager.register(onCeolStatusChangedListener);
    }

    public void volumeUp() {
        ceolCommandManager.execute(new CommandMasterVolume(DirectionType.Up));
    }

    public void volumeDown() {
        ceolCommandManager.execute(new CommandMasterVolume(DirectionType.Down));
    }

    public void skipBackwards() {
        ceolCommandManager.execute(new CommandSkip(DirectionType.Backward));
    }

    public void skipForwards() {
        ceolCommandManager.execute(new CommandSkip(DirectionType.Forward));
    }

    public void performCommand( Command command ) {
        if ( command != null) {
            ceolCommandManager.execute(command);
        }
    }

    public void performMacro() {
        ceolCommandManager.execute(new CommandMacro(1));
    }

    public void activityOnStop() {
        stopListening();
    }

    public void activityOnStart() {
        startListening();
    }

    public void setViewCommandHandlers(View rootView) {

        setClickHandler( rootView, R.id.powerB, new CommandSetPowerToggle());
        setClickHandler( rootView, R.id.performMacro1B, new CommandMacro(1));
        setClickHandler( rootView, R.id.performMacro2B, new CommandMacro(2));
        setClickHandler( rootView, R.id.performMacro3B, new CommandMacro(3));
        setClickHandler( rootView, R.id.volumeupB, new CommandMasterVolumeUp());
        setClickHandler( rootView, R.id.volumedownB, new CommandMasterVolumeDown());
//        setClickHandler( rootView, R.id.fastBackwardsB, new CommandFastBackward());
//        setClickHandler( rootView, R.id.fastForwardsB, new CommandFastForward());
        setClickHandler( rootView, R.id.skipBackwardsB, new CommandSkipBackward());
        setClickHandler( rootView, R.id.skipForwardsB, new CommandSkipForward());
        setClickHandler( rootView, R.id.playpauseB, new CommandControlToggle());
        setClickHandler( rootView, R.id.stopB, new CommandControlStop());
        setClickHandler( rootView, R.id.navLeftB, new CommandCursor(DirectionType.Left));
        setClickHandler( rootView, R.id.navRightB, new CommandCursor(DirectionType.Right));
        setClickHandler( rootView, R.id.navUpB, new CommandCursor(DirectionType.Up));
        setClickHandler( rootView, R.id.navDownB, new CommandCursor(DirectionType.Down));
        setClickHandler( rootView, R.id.navEnterB, new CommandCursorEnter());
        setClickHandler( rootView, R.id.siInternetRadioB, new CommandSetSI(SIStatusType.IRadio));
        setClickHandler( rootView, R.id.siIpodB, new CommandSetSI(SIStatusType.Ipod));
        setClickHandler( rootView, R.id.siMusicServerB, new CommandSetSI(SIStatusType.NetServer));
        setClickHandler( rootView, R.id.siTunerB, new CommandSetSI(SIStatusType.Tuner));
        setClickHandler( rootView, R.id.siAnalogInB, new CommandSetSI(SIStatusType.AnalogIn));
        setClickHandler( rootView, R.id.siDigitalInB, new CommandSetSI(SIStatusType.DigitalIn1));
        setClickHandler( rootView, R.id.siBluetoothB, new CommandSetSI(SIStatusType.Bluetooth));
        setClickHandler( rootView, R.id.siCdB, new CommandSetSI(SIStatusType.CD));

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
        if ( v.getTag() != null && v.getTag() instanceof Command ) {
            Command command = (Command)v.getTag();
            ceolCommandManager.execute(command);
        }
    }
}
