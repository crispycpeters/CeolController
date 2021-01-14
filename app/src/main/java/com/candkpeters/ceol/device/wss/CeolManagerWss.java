package com.candkpeters.ceol.device.wss;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;

import com.candkpeters.ceol.device.CeolManager;
import com.candkpeters.ceol.device.MacroInflater;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.device.command.Command;

/**
 * Created by crisp on 25/01/2016.
 */
public class CeolManagerWss extends CeolManager {

    private static final String TAG = "CeolManager" ;
    private ConnectivityManager connectivityManager;
//    private CeolDeviceWebSvcCommand ceolDeviceWebSvcCommand;
    private boolean isDebugMode;
//    private CeolWebSvcGatherer ceolWebSvcGatherer;
    private WssClient wssClient;

    private MacroInflater macroInflater;

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = null;

    public CeolManagerWss(final Context context) {
        super(context);
        wssClient = new WssClient(ceolModel);
//        ceolWebSvcGatherer = new CeolWebSvcGatherer(context, ceolModel);
//        ceolDeviceWebSvcCommand = new CeolDeviceWebSvcCommand(ceolModel);
    }

    public void sendCommand(String commandString) {
        Log.d(TAG, "sendCommand: Sending: " + commandString);
        if ( isOnWifi() && commandString!= null && !commandString.isEmpty()) {

//            ceolDeviceWebSvcCommand.sendCeolCommand( commandString, null);   //TODO We need use callback
//            ceolWebSvcGatherer.setActive();
            wssClient.sendCommand(commandString);
        }
    }

    public void execute(Command command, OnCeolStatusChangedListener onDoneCeolStatusChangedListener) {
        if ( command!= null ) {
//            command.execute(this, onDoneCeolStatusChangedListener);
            command.execute(this, onDoneCeolStatusChangedListener);
        }
    }

    /**************************
     * ENGINE
     */
    public void engineResumeGatherers()
    {
        if ( isOnWifi()) {
            wssClient.start( getPrefs().getWssServer() );
//            ceolDeviceWebSvcCommand.start(getPrefs());
//            ceolWebSvcGatherer.start(getPrefs());
        }
    }

    public void enginePauseGatherers()
    {
        wssClient.stop();
        ceolModel.notifyConnectionStatus(false);
    }

    public void nudgeGatherers() {
        if ( isOnWifi()) {
            wssClient.start( getPrefs().getWssServer() );
        }
    }

}