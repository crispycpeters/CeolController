package com.candkpeters.ceol.device.command;

import android.util.Log;

import com.candkpeters.ceol.model.SIStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandSetSI extends Command {

    private static final String TAG = "CommandSetSI";
    private SIStatusType value;

    public CommandSetSI(SIStatusType value) {
        super( );
        this.value = value;
    }

    public CommandSetSI() {
        this(SIStatusType.NotConnected);
    }

    public SIStatusType getValue() {
        return value;
    }

    @Override
    protected boolean isSuccessful() {
        return ceolModel.inputControl.getSIStatus() == value;
    }

    @Override
    public void execute() {
        String webCommand;
        switch ( value ) {
            case NotConnected:
                webCommand = null;
                break;
            case CD:
                webCommand = "SICD";
                break;
            case Tuner:
/*
                webCommand = "SIDIGITALIN1";
                ceolManager.sendCommand(webCommand);
                TODO: We need to implement a special change to Tuner if we are changing to Tuner from OpenHome/DLNA
                Just sending SITUNER does not change monitor status away from NET - it's a denon bug
                We'll need to send, say, SIDIGITAL1 then SITUNER (after a short period)
*/

                webCommand = "SITUNER";
                break;
            case IRadio:
                webCommand = "SIIRADIO";
                break;
            case NetServer:
                webCommand = "SISERVER";
                break;
            case AnalogIn:
                webCommand = "SIANALOGIN";
                break;
            case DigitalIn1:
                webCommand = "SIDIGITALIN1";
                break;
            case DigitalIn2:
                webCommand = "SIDIGITALIN2";
                break;
            case Bluetooth:
                webCommand = "SIBLUETOOTH";
                break;
            case Ipod:
                webCommand = "SIUSB";
                break;
            default:
                webCommand = null;
        }
        ceolManager.sendCommand(webCommand);
    }

    @Override
    public String getParameterAsString() {
        return value.toString();
    }

    protected void initialize(String valueString) {
        try {
            value = SIStatusType.valueOf(valueString);
        } catch ( IllegalArgumentException e ) {
            Log.e(TAG, "initialize: Could not initialize SIStatus with: " + valueString + ": Stack: " + e.getStackTrace() );
        }
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(" + value + ")";
    }

}
