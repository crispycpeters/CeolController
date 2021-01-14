package com.candkpeters.ceol.device.websvc;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;

import com.candkpeters.ceol.device.websvc.CeolDeviceWebSvcCommand;
import com.candkpeters.ceol.device.CeolManager;
import com.candkpeters.ceol.device.MacroInflater;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.device.websvc.CeolWebSvcGatherer;

/**
 * Created by crisp on 25/01/2016.
 */
public class CeolManagerWebSvc extends CeolManager {

    private static final String TAG = "CeolManager" ;
    private ConnectivityManager connectivityManager;
    private CeolDeviceWebSvcCommand ceolDeviceWebSvcCommand;
    private boolean isDebugMode;
    private CeolWebSvcGatherer ceolWebSvcGatherer;

    private MacroInflater macroInflater;

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = null;

    public CeolManagerWebSvc(final Context context) {
        super(context);
        ceolWebSvcGatherer = new CeolWebSvcGatherer(context, ceolModel);
        ceolDeviceWebSvcCommand = new CeolDeviceWebSvcCommand(ceolModel);
    }

    public void sendCommand(String commandString) {
        Log.d(TAG, "sendCommand: Sending: " + commandString);
        if ( isOnWifi() && commandString!= null && !commandString.isEmpty()) {
            ceolDeviceWebSvcCommand.sendCeolCommand( commandString, null);   //TODO We need use callback
            ceolWebSvcGatherer.setActive();
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
            ceolDeviceWebSvcCommand.start(getPrefs());
            ceolWebSvcGatherer.start(getPrefs());
        }
    }

    public void enginePauseGatherers()
    {
        ceolDeviceWebSvcCommand.stop();
        ceolWebSvcGatherer.pause();
        ceolModel.notifyConnectionStatus(false);
    }

    public void nudgeGatherers() {
        if ( isOnWifi()) {
            ceolWebSvcGatherer.setActive();
        }

    }

}