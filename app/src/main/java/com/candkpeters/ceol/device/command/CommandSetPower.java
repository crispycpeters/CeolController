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

        if ( getValue() && ceolModel.powerControl.getDeviceStatus() == DeviceStatusType.On ) {
            result = true;
        } else if ( !getValue() && ceolModel.powerControl.getDeviceStatus()==DeviceStatusType.Standby ) {
            result = true;
        }
        return result;
    }

    @Override
    public void execute() {
        DeviceStatusType status = ceolModel.powerControl.getDeviceStatus();
        if ( status != DeviceStatusType.Starting) {
            ceolManager.sendCommand(getValue() ? "PWON" : "PWSTANDBY");
        }
    }

}
