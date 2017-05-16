package com.candkpeters.ceol.device;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

import com.candkpeters.ceol.cling.ClingGatherer;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.model.control.AudioControl;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.control.CeolNavigatorControl;
import com.candkpeters.ceol.model.control.ConnectionControl;
import com.candkpeters.ceol.model.control.ControlBase;
import com.candkpeters.ceol.model.control.InputControl;
import com.candkpeters.ceol.model.OnControlChangedListener;
import com.candkpeters.ceol.model.control.PowerControl;
import com.candkpeters.ceol.model.control.TrackControl;
import com.candkpeters.ceol.view.Prefs;

import java.util.ArrayList;

/**
 * Created by crisp on 25/01/2016.
 */
public class CeolManager2 {

    private static final String TAG = "CeolManager2" ;
    public final CeolModel ceolModel;
    private final Context context;
//    private final CeolDeviceObserver_DELETE ceolDeviceObserver;
//    private final ClingManager clingManager;
    private CeolDeviceWebSvcCommand ceolDeviceWebSvcCommand;

    private final CeolWebSvcGatherer ceolWebSvcGatherer;
    private final ClingGatherer clingGatherer;

    private MacroInflater macroInflater;

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = null;

    private String message;

    public CeolManager2(final Context context) {
        this.context = context;
        ceolModel = new CeolModel();
//        ceolDeviceObserver = new CeolDeviceObserver_DELETE();
//DELETE        ceolDevice = new CeolDevice_DELETE(ceolDeviceObserver);
//        clingManager = new ClingManager(context, ceolDevice);
        ceolWebSvcGatherer = new CeolWebSvcGatherer(ceolModel);
        clingGatherer = new ClingGatherer(context, ceolModel);
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
//        clingManager.bindToCling();
    }

    private void updateConfig(Context context) {
        Prefs prefs = new Prefs(context);
        inputUpdated(ceolModel.inputControl);
        ceolWebSvcGatherer.stop();
        ceolWebSvcGatherer.start(prefs);

        if ( ceolDeviceWebSvcCommand == null ) {
            ceolDeviceWebSvcCommand = new CeolDeviceWebSvcCommand(prefs.getBaseUrl());
        } else {
            ceolDeviceWebSvcCommand.recreateService(prefs.getBaseUrl());
        }
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

    public void register(OnControlChangedListener obj) {
        ceolModel.register(obj);
        // TODO: Potentially unpause ClingManager events if paused
    }

    public void unregister(OnControlChangedListener obj) {
        ceolModel.unregister(obj);
        // TODO: Potentially pause ClingManager events if nothing is registered to listen
    }

    public void notifyObservers(ControlBase controlBase) {
        ceolModel.notifyObservers(controlBase);
    }

    public void sendCommand(String commandString) {
        Log.d(TAG, "sendCommand: Sending: " + commandString);
        if ( commandString!= null && !commandString.isEmpty()) {
            ceolDeviceWebSvcCommand.SendCommand( commandString, null);   //TODO We need use callback
            ceolWebSvcGatherer.getStatusSoon();
        }
    }

    public void execute(Command command) {
        if ( command!= null ) {
//            command.execute(this);
            command.execute(this);
        }
    }

    public void execute(Command command, OnCeolStatusChangedListener onDoneCeolStatusChangedListener) {
        if ( command!= null ) {
//            command.execute(this, onDoneCeolStatusChangedListener);
            command.execute(this, onDoneCeolStatusChangedListener);
        }
    }


    public void start() {
        ceolModel.register(new OnControlChangedListener() {
            @Override
            public void onCAudioControlChanged(CeolModel ceolModel, AudioControl audioControl) {

            }

            @Override
            public void onConnectionControlChanged(CeolModel ceolModel, ConnectionControl connectionControl) {
                connectionUpdated( connectionControl);
            }

            @Override
            public void onCeolNavigatorControlChanged(CeolModel ceolModel, CeolNavigatorControl ceolNavigatorControl) {

            }

            @Override
            public void onInputControlChanged(CeolModel ceolModel, InputControl inputControl) {
                inputUpdated( inputControl);
            }

            @Override
            public void onPowerControlChanged(CeolModel ceolModel, PowerControl powerControl) {

            }

            @Override
            public void onTrackControlChanged(CeolModel ceolModel, TrackControl trackControl) {

            }

        });
    }

    private void connectionUpdated(ConnectionControl connectionControl) {
        //TODO - pause relevant gatherers if disconnected

    }

    private void inputUpdated(InputControl inputControl) {
        switch (inputControl.getStreamingStatus()) {

            case CEOL:
            case SPOTIFY:
                Prefs prefs = new Prefs(context);
                ceolWebSvcGatherer.start(prefs);
                break;
            case DLNA:
            case OPENHOME:
                break;
            case NONE:
                break;
        }
        //TODO

    }

    public void sendOpenHomeCommand(String commandString) {
        if (context != null) {
            clingGatherer.getOpenHomeUpnpDevice().performPlaylistCommand(commandString);
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
                    final String CMDSTOP = "stop";

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

    public void stop() {
        clingGatherer.stop();
    }
}