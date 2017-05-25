package com.candkpeters.ceol.device;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

import com.candkpeters.ceol.cling.ClingManager;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.model.CeolDevice_DELETE;
import com.candkpeters.ceol.view.Prefs;

import java.util.ArrayList;

/**
 * Created by crisp on 25/01/2016.
 */
public class CeolManager_DELETE {

    private static final String TAG = "CeolManager_DELETE" ;
    private final CeolDevice_DELETE ceolDevice;
    private final Context context;
    private final CeolDeviceObserver_DELETE ceolDeviceObserver;
    private final ClingManager clingManager;
    private CeolDeviceWebSvcMonitor_DELETE ceolDeviceMonitor;
    private CeolDeviceWebSvcCommand ceolDeviceWebSvcCommand;
    private MacroInflater macroInflater;

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = null;

    private String message;

    public CeolManager_DELETE(final Context context) {
        this.context = context;
        ceolDeviceObserver = new CeolDeviceObserver_DELETE();
        ceolDevice = new CeolDevice_DELETE(ceolDeviceObserver);
        clingManager = new ClingManager(context, ceolDevice);
    }

    /*
    To be called when config changes or on start
     */
    public void initialize() {
        if (onSharedPreferenceChangeListener == null) {
            Prefs prefs = new Prefs(context);
            onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    updateConfig(context);
                }
            };
            prefs.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        }
        updateConfig(context);
        clingManager.bindToCling();
    }

    private void updateConfig(Context context) {
        Prefs prefs = new Prefs(context);
        if ( ceolDeviceMonitor == null) {
            ceolDeviceMonitor = new CeolDeviceWebSvcMonitor_DELETE(getCeolDevice(), prefs.getBaseUrl());
        } else {
            ceolDeviceMonitor.recreateService(prefs.getBaseUrl());
        }
/*
        if ( ceolDeviceWebSvcCommand == null ) {
            ceolDeviceWebSvcCommand = new CeolDeviceWebSvcCommand(prefs.getBaseUrl());
        } else {
            ceolDeviceWebSvcCommand.recreateService(prefs.getBaseUrl());
        }
*/
        macroInflater = new MacroInflater(prefs.getMacroNames(), prefs.getMacroValues());
    }

    public ArrayList<Command> getMacro(int macroNumber) {
        ArrayList<Command> commands;
        commands = macroInflater.getMacro(macroNumber);

        if ( commands == null ) {
            commands = new ArrayList<Command>();
        }
        return commands;
    }

    public void register(OnCeolStatusChangedListener obj) {
//        ceolDeviceMonitor.register(obj);
        if ( ceolDeviceObserver.register(obj) == 1) {
            ceolDeviceMonitor.startActiveUpdates();
        }
        // TODO: Potentially unpause ClingManager events if paused
    }

    public void unregister(OnCeolStatusChangedListener obj) {
        if ( ceolDeviceObserver.unregister(obj) == 0 ) {
            ceolDeviceMonitor.stopActiveUpdates();
        };
        // TODO: Potentially pause ClingManager events if nothing is registered to listen
    }

    public void notifyObservers() {
//        ceolDeviceMonitor.notifyObservers();
        ceolDeviceObserver.notifyObservers();
    }

    public void sendCommand(String commandString) {
        Log.d(TAG, "sendCommand: Sending: " + commandString);
        if ( commandString!= null && !commandString.isEmpty()) {
            ceolDeviceWebSvcCommand.sendCeolCommand( commandString, null);   //TODO We need use callback
            ceolDeviceMonitor.getStatusSoon();
        }
    }

    public void execute(Command command) {
        if ( command!= null ) {
//            command.execute(this);
        }
    }

    public void execute(Command command, OnCeolStatusChangedListener onDoneCeolStatusChangedListener) {
        if ( command!= null ) {
//            command.execute(this, onDoneCeolStatusChangedListener);
        }
    }

    public CeolDevice_DELETE getCeolDevice() {
        return ceolDevice;
    }

    public void start() {
//TODO - TEST MODE FOR CEOLMANAGER2        ceolDeviceMonitor.start();
    }

    public void sendOpenHomeCommand(String commandString) {
        if (context != null) {
            clingManager.getOpenHomeUpnpDevice().performPlaylistCommand(commandString);
        }
    }

    public void sendSpotifyCommand(String commandString) {
        if ( context != null ) {
            if ( commandString.equalsIgnoreCase("PLAY")) {
                try {
                    /*
                     * ISSUE - Not working yet - it starts then stops
                     */
                    final String CMDTOGGLEPAUSE = "togglepause";
                    final String CMDPAUSE = "pause";
                    final String CMDPREVIOUS = "previous";
                    final String CMDNEXT = "next";
                    final String SERVICECMD = "com.android.music.musicservicecommand";
                    final String CMDNAME = "command";
                    final String CMDSTOP = "destroy";


                    AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

                    if(mAudioManager.isMusicActive()) {
                        Intent i = new Intent(SERVICECMD);
                        i.putExtra(CMDNAME , CMDTOGGLEPAUSE );
                        context.sendBroadcast(i);
                    }
                    /*
                    Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
                    i.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.internal.receiver.MediaButtonReceiver"));
                    i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
                    context.sendOrderedBroadcast(i, null);

                    //release the button
                    i = new Intent(Intent.ACTION_MEDIA_BUTTON);
                    i.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.internal.receiver.MediaButtonReceiver"));
                    i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
                    context.sendOrderedBroadcast(i, null);
*/
                } catch (Exception exc) {
                    Log.d(TAG, "sendSpotifyCommand: Got exception:" + exc.toString());
                }
            }
            Intent intent = new Intent("com.spotify.mobile.android.ui.widget." + commandString);
            context.sendBroadcast(intent);
        }
    }
}