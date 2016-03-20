package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.SIStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandSetSI extends Command {

    private SIStatusType value;

    public CommandSetSI(SIStatusType value) {
        super( );
        this.value = value;
    }

    public CommandSetSI() {
        this(SIStatusType.Unknown);
    }

    public SIStatusType getValue() {
        return value;
    }

    @Override
    protected boolean isSuccessful() {
        return ceolDevice != null && ceolDevice.getSIStatus() == value;
    }

    @Override
    public void execute() {
        String webCommand;
        switch ( value ) {
            case Unknown:
                webCommand = null;
                break;
            case CD:
                webCommand = "SICD";
                break;
            case Tuner:
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
        ceolCommandManager.sendCommand(webCommand);
    }

    @Override
    public String getParameterAsString() {
        return value.toString();
    }

    protected void initialize(String valueString) {
        value = SIStatusType.valueOf(valueString);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(" + value + ")";
    }

}
