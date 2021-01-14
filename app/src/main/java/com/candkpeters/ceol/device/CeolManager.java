package com.candkpeters.ceol.device;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.candkpeters.ceol.cling.ClingGatherer2;
import com.candkpeters.ceol.cling.OnClingListener;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.OnControlChangedListener;
import com.candkpeters.ceol.view.Prefs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by crisp on 25/01/2016.
 */
public abstract class CeolManager {

    private static final String TAG = "CeolManager" ;
    public final CeolModel ceolModel;
    private final Context context;
    private ConnectivityManager connectivityManager;
    private boolean isDebugMode;
    private final ClingGatherer2 clingGatherer;

    private MacroInflater macroInflater;

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = null;

    public CeolManager(final Context context) {
        this.context = context;
        this.ceolModel = new CeolModel();
        clingGatherer = new ClingGatherer2(context, ceolModel, new OnClingListener() {
                @Override
                public void onClingDisconnected() {
//                if ( haveNetwork) {
//                    Log.d(TAG, "onClingDisconnected: We were disconnected. Let's connect again.");
//                    startGatherers();
//                }
            }
        });
    }

    private static int MAX_LOGSIZE = 5000;
    private String logMessages[] = new String[MAX_LOGSIZE];
    private int logPosition = 0;

    public void logd(String tag, String msg) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String datestr = df.format(new Date());

        synchronized (logMessages) {
            logMessages[logPosition] = String.format("%s D/%s: %s",datestr,tag,msg);
            logPosition = (logPosition + 1) % MAX_LOGSIZE;
        }
        Log.d(tag, msg);
    }

    public String[] getLogItems() {
        return logMessages;
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
                    engineRestartGatherers(context);
                }
            };
            prefs.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        }
        engineResumeGatherers();
    }

    public boolean isOnWifi() {
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
            pauseCling();
        }
*/
        // TODO: Potentially pauseCling openhome events if nothing is registered to listen
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

    protected Prefs getPrefs() {
        Prefs prefs = new Prefs(context);
        return prefs;
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
                    final String CMDPAUSE = "pauseCling";
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

    /**************************
     * ENGINE
     */
    public abstract void engineResumeGatherers();

    public abstract void enginePauseGatherers();

    public abstract void sendCommand(String commandString);

    private void engineRestartGatherers(Context context) {
        Prefs prefs = new Prefs(context);
        isDebugMode = prefs.getIsDebugMode();

//        inputUpdated(ceolModel.inputControl);
        enginePauseGatherers();
        engineResumeGatherers();

        macroInflater = new MacroInflater(prefs.getMacroNames(), prefs.getMacroValues());
    }

    public void stopCling() {
        clingGatherer.pause();
    }

    public abstract void nudgeGatherers();

/*
    public boolean checkConnectivity() {
        if ( isOnWifi()) {
            startGatherers();
            return true;
        } else {
            stopGatherers();
            return false;
        }
    }
*/
}