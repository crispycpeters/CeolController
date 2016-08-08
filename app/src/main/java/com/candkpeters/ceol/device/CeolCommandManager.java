package com.candkpeters.ceol.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.view.Prefs;

import java.util.ArrayList;

/**
 * Created by crisp on 25/01/2016.
 */
public class CeolCommandManager {

    private static final String TAG = "CeolCommandManager" ;
    private CeolDevice device;
    private CeolDeviceWebSvcMonitor ceolDeviceMonitor;
    private CeolDeviceWebSvcCommand ceolDeviceWebSvcCommand;
//    private int webStatusRepeatRateMsecs;
    private static CeolCommandManager ourInstance = new CeolCommandManager();

    private MacroInflater macroInflater;

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = null;

//    private List<OnCeolStatusChangedListener> observers;
    private String message;
//    private final Object MUTEX = new Object();

    private CeolCommandManager() {
    }

    public static CeolCommandManager getInstance() {
        return ourInstance;
    }

    /*
    To be called when config changes or on start
     */
    public void initialize(final Context context) {

        if ( this.device == null ) {
            this.device = CeolDevice.getInstance();
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
        }
    }

    private void updateConfig(Context context) {
        Prefs prefs = new Prefs(context);
        if ( ceolDeviceMonitor == null) {
            ceolDeviceMonitor = new CeolDeviceWebSvcMonitor(prefs.getBaseUrl(),
                    prefs.getBackgroundTimeoutSecs() * 1000,
                    prefs.getBackgroundRateSecs() * 1000);
        } else {
            ceolDeviceMonitor.recreateService(prefs.getBaseUrl());
        }
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

    public void register(OnCeolStatusChangedListener obj) {
        ceolDeviceMonitor.register(obj);
    }

    public void unregister(OnCeolStatusChangedListener obj) {
        ceolDeviceMonitor.unregister(obj);
    }

    public void sendCommand(String commandString) {
        Log.d(TAG, "sendCommand: Sending: " + commandString);
        if ( commandString!= null && commandString != "") {
            ceolDeviceWebSvcCommand.SendCommand( commandString, null);   //TODO We need use callback
            ceolDeviceMonitor.getStatusSoon();
        }
    }

    public void execute(Command command) {
        if ( command!= null ) {
            command.execute(this);
        }
    }

    public void execute(Command command, OnCeolStatusChangedListener onDoneCeolStatusChangedListener) {
        if ( command!= null ) {
            command.execute(this, onDoneCeolStatusChangedListener);
        }
    }

    public CeolDevice getCeolDevice() {
        return CeolDevice.getInstance();
    }

    public void start() {
        ceolDeviceMonitor.start();
    }
}