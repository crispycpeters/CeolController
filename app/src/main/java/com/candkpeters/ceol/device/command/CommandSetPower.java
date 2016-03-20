package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.CommandType;
import com.candkpeters.ceol.model.DeviceStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandSetPower extends CommandBaseBoolean {

    boolean isToggle = false;

    public CommandSetPower() {
        super(false);
        isToggle = true;
    }
    public CommandSetPower(boolean onOff) {
        super( onOff);
        isToggle = false;
    }

    @Override
    protected boolean isSuccessful() {
        if ( isToggle) {
            // TODO Not sure how to achieve this check
            return true;
        } else {
            if (getValue()) {
                if (ceolDevice.getDeviceStatus() == DeviceStatusType.On) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (ceolDevice.getDeviceStatus() == DeviceStatusType.Standby) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    @Override
    public void execute() {
        DeviceStatusType status = ceolDevice.getDeviceStatus();
        if ( status != DeviceStatusType.Starting) {
            if (isToggle) {
                if (ceolDevice.getDeviceStatus() == DeviceStatusType.On) {
                    ceolCommandManager.sendCommand("PWSTANDBY");
                } else {
                    ceolCommandManager.sendCommand("PWON");
                }
            } else {
                ceolCommandManager.sendCommand(getValue() ? "PWON" : "PWSTANDBY");
            }
        }
    }

}
