package com.candkpeters.ceol.device.wss;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

import com.candkpeters.ceol.device.CeolEngine;
import com.candkpeters.ceol.device.CeolManager;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.model.CeolModel;

/**
 * Created by crisp on 25/01/2016.
 */
public class CeolEngineWss extends CeolEngine {

    private static final String TAG = "CeolManager" ;
    private final ConnectionStateMonitor connectionStateMonitor;
    private ConnectivityManager connectivityManager;
    private boolean isDebugMode;
    private final WssClient wssClient;
    private boolean isStarted = false;

    public CeolEngineWss(final Context context, final CeolModel ceolModel) {
        super(context, ceolModel);
        Log.d(TAG, "CeolEngineWss: Creating WssClient");
        wssClient = new WssClient(ceolModel);
        connectionStateMonitor = new ConnectionStateMonitor();
    }

    public void sendCommand(String commandString) {
    }

    /**************************
     * ENGINE
     */
    @Override
    public void start() {

        connectionStateMonitor.enable(context, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable( Network network) {
                Log.d(TAG, "WIFI is connected");
                // Restart for quicker detection
                if ( isOnWifi() ) {
                    wssClient.start(getPrefs().getWssServer());
                } else {
                    wssClient.stop();
                    ceolModel.notifyConnectionStatus(false);
                }
            }

            @Override
            public void onLost(Network network) {
                Log.d(TAG, "WIFI is disconnected");
                if ( isOnWifi()) {
                    wssClient.start(getPrefs().getWssServer());
                } else {
                    wssClient.stop();
                    ceolModel.notifyConnectionStatus(false);
                }
            }
        });
//        wssClient.start( getPrefs().getWssServer() );
        isStarted = true;

    }

    private boolean isOnWifi() {
        if ( connectivityManager == null ) {
            connectivityManager =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if ( isConnected ) {
            if ( activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.i(TAG, "isOnWifi: WiFi is active");
                return true;
            } else {
                Log.i(TAG, "isOnWifi: On network but not WiFi");
                return false;
            }
        } else {
            Log.i(TAG, "isOnWifi: No network");
            return false;
        }
    }


    @Override
    public void stop() {
        wssClient.stop();
        ceolModel.notifyConnectionStatus(false);
        isStarted = false;
    }

    @Override
    public void nudge() {
        ceolModel.notifyAllObservers();
    }

    @Override
    public void sendCommandStr(String commandString) {
        Log.d(TAG, "sendCommandStr: Sending: " + commandString);
        if ( commandString!= null && !commandString.isEmpty()) {
            wssClient.sendCommand(commandString);
        }

    }

    @Override
    public void sendCommandSeekTrack(int trackId) {
        Log.d(TAG, "sendCommandSeekTrack: Sending: " + trackId);
        wssClient.sendCommand("OHCMD_SEEKID:" + trackId);
    }

    @Override
    public void sendCommandSeekAbsoluteSecond(int absoluteSeconds) {
        Log.d(TAG, "sendCommandSeekAbsoluteSecond: Sending: " + absoluteSeconds);
        wssClient.sendCommand("OHCMD_SEEK:" + absoluteSeconds);
    }
}