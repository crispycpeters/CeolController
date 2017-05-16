package com.candkpeters.ceol.model.control;

import com.candkpeters.ceol.model.DeviceStatusType;

import static com.candkpeters.ceol.model.DeviceStatusType.Standby;
import static com.candkpeters.ceol.model.DeviceStatusType.Starting;

/**
 * Created by crisp on 03/05/2017.
 */

public class PowerControl extends ControlBase {

    private static final String TAG = "PowerControl";
    private static final long DEFAULT_WAKEUP_PERIOD_MSECS = 3000;  // Give machine a chance to wake up before sending commands
    private static final long wakeUpPeriodMsecs = DEFAULT_WAKEUP_PERIOD_MSECS;

    protected DeviceStatusType deviceStatus = DeviceStatusType.Connecting;
    protected long deviceOnTimeMsecs = -1;

    public PowerControl() {
    }

    public DeviceStatusType getDeviceStatus() {
        return deviceStatus;
    }

    @Override
    public boolean copyFrom(ControlBase newControl) {
        boolean hasChanged = false;
        if ( newControl != null && newControl instanceof PowerControl) {
            PowerControl newPowerControl = (PowerControl)newControl;
            if (this.deviceStatus != newPowerControl.deviceStatus) {
                this.deviceStatus = newPowerControl.deviceStatus;
                hasChanged = true;
            }
            // Don't need to inform anyone when timer changes
            this.deviceOnTimeMsecs = newPowerControl.deviceOnTimeMsecs;
        }
        return hasChanged;
    }

    public boolean updateDeviceStatus(String powerString) {
        boolean hasChanged = false;

        boolean deviceSaysOn = powerString.equals("ON");
        long now = System.currentTimeMillis();
        if (deviceSaysOn) {
            switch (deviceStatus) {
                case Connecting:
                    // We don't know how long the device has been on, just assume long enough
                    updateDeviceStatus(DeviceStatusType.On);
                    deviceOnTimeMsecs = now - wakeUpPeriodMsecs;
                    hasChanged = true;
                    break;
                case Standby:
                    // We are switching on. Start a timer before we try to communicate
                    deviceOnTimeMsecs = now;
                    updateDeviceStatus(Starting);
                    hasChanged = true;
                    break;
                case Starting:
                    if ((now - deviceOnTimeMsecs) > wakeUpPeriodMsecs) {
                        updateDeviceStatus(DeviceStatusType.On);
                        hasChanged = true;
                    }
                    break;
                case On:
                    break;
            }
        } else {
            hasChanged = updateDeviceStatus(Standby);
        }
        return hasChanged;
    }

    public boolean updateDeviceStatus(DeviceStatusType deviceStatus) {
        if ( this.deviceStatus != deviceStatus) {
            this.deviceStatus = deviceStatus;
            return true;
        } else {
            return false;
        }
    }


}
