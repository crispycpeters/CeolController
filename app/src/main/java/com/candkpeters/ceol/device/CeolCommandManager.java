package com.candkpeters.ceol.device;

import android.util.Log;

import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.model.CeolDevice;

import java.util.ArrayList;
import java.util.List;

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
    public void setDevice(CeolDevice device, String baseUrl, String[] macroNames, String[] macroValues) {
//        if ( this.device == null ) {
            this.device = CeolDevice.getInstance();
//        }
//        if ( this.ceolDeviceMonitor == null ) {
            this.ceolDeviceMonitor = new CeolDeviceWebSvcMonitor(baseUrl);
//        }
//        if ( this.ceolDeviceWebSvcCommand == null ) {
            this.ceolDeviceWebSvcCommand = new CeolDeviceWebSvcCommand(baseUrl);
//        }
//        this.webStatusRepeatRateMsecs = webStatusRepeatRateMsecs;
//        this.observers=new ArrayList<OnCeolStatusChangedListener>();
        macroInflater = new MacroInflater(macroNames, macroValues);
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