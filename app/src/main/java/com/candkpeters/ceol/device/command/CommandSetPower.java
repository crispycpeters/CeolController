package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.DeviceStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandSetPower extends CommandBaseBoolean {

    public CommandSetPower() {
        super(false);
    }
    public CommandSetPower(boolean onOff) {
        super( onOff);
    }

    @Override
    protected boolean isSuccessful() {
        boolean result = false;

        if ( getValue() && ceolDevice.getDeviceStatus() == DeviceStatusType.On ) {
            result = true;
        } else if ( !getValue() && ceolDevice.getDeviceStatus()==DeviceStatusType.Standby ) {
            result = true;
        }
        return result;
    }

    @Override
    public void execute() {
        DeviceStatusType status = ceolDevice.getDeviceStatus();
        if ( status != DeviceStatusType.Starting) {
            ceolCommandManager.sendCommand(getValue() ? "PWON" : "PWSTANDBY");
        }
    }

}
