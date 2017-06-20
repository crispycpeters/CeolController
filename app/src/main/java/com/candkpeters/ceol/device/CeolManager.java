package com.candkpeters.ceol.device;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

import com.candkpeters.ceol.cling.ClingGatherer;
import com.candkpeters.ceol.cling.ClingGatherer2;
import com.candkpeters.ceol.cling.OnClingListener;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.OnControlChangedListener;
import com.candkpeters.ceol.view.Prefs;

import java.util.ArrayList;

/**
 * Created by crisp on 25/01/2016.
 */
public class CeolManager {

    private static final String TAG = "CeolManager" ;
    public final CeolModel ceolModel;
    private final Context context;
    private CeolDeviceWebSvcCommand ceolDeviceWebSvcCommand;
    private boolean isDebugMode;
    private final CeolWebSvcGatherer ceolWebSvcGatherer;
//    private final ClingGatherer clingGatherer;
    private final ClingGatherer2 clingGatherer;

    private MacroInflater macroInflater;

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = null;

    private boolean haveNetwork = true;

    public CeolManager(final Context context) {
        this.context = context;
        ceolModel = new CeolModel();
        ceolWebSvcGatherer = new CeolWebSvcGatherer(context, ceolModel);
//        clingGatherer = new ClingGatherer(context, ceolModel, new OnClingListener() {
//                @Override
//                public void onClingDisconnected() {
//                    if ( haveNetwork) {
//                        Log.d(TAG, "onClingDisconnected: We were disconnected. Let's connect again.");
//                        startGatherers();
//                    }
//                }
//            });
        clingGatherer = new ClingGatherer2(context, ceolModel, new OnClingListener() {
            @Override
            public void onClingDisconnected() {
                if ( haveNetwork) {
                    Log.d(TAG, "onClingDisconnected: We were disconnected. Let's connect again.");
                    startGatherers();
                }
            }
        });
        ceolDeviceWebSvcCommand = new CeolDeviceWebSvcCommand(ceolModel);
    }

    /*
    To be called when config changes or on start
     */
    public void initialize() {
        if (onSharedPreferenceChangeListener == null) {
            Prefs prefs = new Prefs(context);
            isDebugMode = prefs.getIsDebugMode();
            onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    restart(context);
                }
            };
            prefs.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        }
        startGatherers();
    }

    public ArrayList<Command> getMacro(int macroNumber) {
        ArrayList<Command> commands;
        commands = macroInflater.getMacro(macroNumber);

        if ( commands == null ) {
            commands = new ArrayList<>();
        }
        return commands;
    }

    public void register(OnControlChangedListener obj) {
        ceolModel.register(obj);
        // Ensure all gatherers are running
//        inputUpdated(ceolModel.inputControl);
        // TODO: Potentially unpause openhome events if paused
    }

    public void unregister(OnControlChangedListener obj) {
        ceolModel.unregister(obj);
/*
        int numRegistered = ceolModel.registerCount();
        if ( numRegistered <= 1) {
            pause();
        }
*/
        // TODO: Potentially pause openhome events if nothing is registered to listen
    }

    public void sendCommand(String commandString) {
        Log.d(TAG, "sendCommand: Sending: " + commandString);
        if ( commandString!= null && !commandString.isEmpty()) {
            ceolDeviceWebSvcCommand.sendCeolCommand( commandString, null);   //TODO We need use callback
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



    public boolean isDebugMode() {
        return isDebugMode;
    }

    public void sendOpenHomeCommand(String commandString) {
        clingGatherer.sendOpenHomeCommand(commandString);
    }

    public void sendOpenHomeSeekIdCommand(int trackId) {
        clingGatherer.sendOpenHomeSeekIdCommand(trackId);
    }

    public void sendOpenHomeSeekAbsoluteSecond(int absoluteSeconds) {
        clingGatherer.sendOpenHomeSeekSecondAbsolute(absoluteSeconds);
    }

    public void sendSpotifyCommand(String commandString) {
        if ( context != null ) {
            if ( commandString.equalsIgnoreCase("PLAY")) {
                try {
                    /*
                     * TODO - Not working yet - it starts then stops
                     */
                    final String CMDTOGGLEPAUSE = "togglepause";
                    final String CMDPAUSE = "pause";
                    final String CMDPREVIOUS = "previous";
                    final String CMDNEXT = "next";
                    final String SERVICECMD = "com.android.music.musicservicecommand";
                    final String CMDNAME = "command";
                    final String CMDSTOP = "stopGatherers";

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

    private void restart(Context context) {
        Prefs prefs = new Prefs(context);
        isDebugMode = prefs.getIsDebugMode();
//        inputUpdated(ceolModel.inputControl);
        ceolWebSvcGatherer.stop();
        ceolWebSvcGatherer.start(prefs);
        clingGatherer.stop();
        clingGatherer.start(prefs);
        ceolDeviceWebSvcCommand.stop();
        ceolDeviceWebSvcCommand.start(prefs);

        macroInflater = new MacroInflater(prefs.getMacroNames(), prefs.getMacroValues());
    }

    public void startGatherers() {
        Prefs prefs = new Prefs(context);
        ceolDeviceWebSvcCommand.start(prefs);
        ceolWebSvcGatherer.start(prefs);
        clingGatherer.start(prefs);
    }

    public void stopGatherers() {
        ceolWebSvcGatherer.stop();
        clingGatherer.stop();
    }

    public void pauseGatherers() {

    }

    public void resumeGatherers() {
        Prefs prefs = new Prefs(context);
        ceolWebSvcGatherer.start(prefs);
        clingGatherer.start(prefs);
        ceolModel.notifyAllObservers();
  }

    public void networkBack() {
        haveNetwork = true;
        Log.d(TAG, "networkBack: ");
        Prefs prefs = new Prefs(context);
        ceolWebSvcGatherer.start(prefs);
        clingGatherer.start(prefs);
    }

    public void networkGone() {
        haveNetwork = false;
        Log.d(TAG, "networkGone: ");
        ceolWebSvcGatherer.stop();
//        clingGatherer.stop();
        ceolModel.notifyConnectionStatus(false);
    }

    public void stopCling() {
        clingGatherer.stop();
    }

}