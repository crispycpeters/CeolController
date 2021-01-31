package com.candkpeters.ceol.device.wss;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
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
    private final ConnectionStateMonitor connectionStateMonitor;
    private final Context context;
    private ConnectivityManager connectivityManager;
    private boolean isDebugMode;
    private WssClient wssClient;
    private boolean isStarted = false;

    public CeolManagerWss(final Context context) {
        super(context);
        this.context = context;
        wssClient = new WssClient(ceolModel);
        connectionStateMonitor = new ConnectionStateMonitor();
    }

    public void sendCommand(String commandString) {
        Log.d(TAG, "sendCommand: Sending: " + commandString);
        if ( commandString!= null && !commandString.isEmpty()) {
            wssClient.sendCommand(commandString);
        }
    }

    public void execute(Command command, OnCeolStatusChangedListener onDoneCeolStatusChangedListener) {
        if ( command!= null ) {
            command.execute(this, onDoneCeolStatusChangedListener);
        }
    }

    /**************************
     * ENGINE
     */
    public void engineResumeGatherers()
    {
        connectionStateMonitor.enable(context, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                Log.d(TAG, "WIFI is connected");
                // Restart for quicker detection
                wssClient.start( getPrefs().getWssServer() );
            }

            @Override
            public void onLost(Network network) {
                Log.d(TAG, "WIFI is disconnected");
                ceolModel.notifyConnectionStatus(false);
            }
        });
        wssClient.start( getPrefs().getWssServer() );
        isStarted = true;
    }

    public void engineStopGatherers()
    {
        wssClient.stop();
        ceolModel.notifyConnectionStatus(false);
        isStarted = false;
    }

    public void nudgeGatherers() {
        wssClient.nudge();
    }

}