package com.candkpeters.ceol.controller;

import android.content.Context;
import android.util.Log;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.device.CeolDeviceWebSvcMonitor;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.device.command.CommandMacro;
import com.candkpeters.ceol.device.command.CommandMasterVolume;
import com.candkpeters.ceol.device.command.CommandSkip;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.ceol.view.Prefs;

//import org.apache.commons.net.telnet.EchoOptionHandler;
//import org.apache.commons.net.telnet.SuppressGAOptionHandler;
//import org.apache.commons.net.telnet.TelnetClient;
//import org.apache.commons.net.telnet.TerminalTypeOptionHandler;


/**
 * Created by crisp on 07/01/2016.
 */
public class CeolController {
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

    public void performMacro() {
        ceolCommandManager.execute(new CommandMacro(1));
    }

    public void activityOnStop() {
        stopListening();
    }

    public void activityOnStart() {
        startListening();
    }
}
