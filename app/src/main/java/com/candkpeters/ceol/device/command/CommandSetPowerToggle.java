package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.DeviceStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandSetPowerToggle extends CommandSetPower {

    public CommandSetPowerToggle() {
        super(false);
    }

    @Override
    protected boolean isSuccessful() {
        boolean result = false;
        // Todo Not sure how to achieve this check

        return result;
    }

    @Override
    public void execute() {
        DeviceStatusType status = ceolModel.powerControl.getDeviceStatus();
        if ( status != DeviceStatusType.Starting) {
            if (ceolModel.powerControl.getDeviceStatus() == DeviceStatusType.On) {
                ceolManager.sendCommand("PWSTANDBY");
            } else {
                ceolManager.sendCommand("PWON");
            }
        }
    }

}
