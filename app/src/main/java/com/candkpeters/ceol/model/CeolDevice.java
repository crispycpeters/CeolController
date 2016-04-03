package com.candkpeters.ceol.model;

import android.util.Log;

/**
 * Created by crisp on 06/01/2016.
 */
public class CeolDevice {

    private static final String TAG = "CeolDevice";
    private static final long DEFAULT_WAKEUP_PERIOD_MSECS = 14000;  // Give machine a chance to wake up before sending commands
    private static final long DEFAULT_NETSERVERON_PERIOD_MSECS = 6000; // Give NetServer a chance to settle down be believing its settings

    private static CeolDevice ourInstance = new CeolDevice();
    private long wakeUpPeriodMsecs = DEFAULT_WAKEUP_PERIOD_MSECS;
    private long netServerOnPeriodMsecs = DEFAULT_NETSERVERON_PERIOD_MSECS;

    public static CeolDevice getInstance() {
        return ourInstance;
    }

    // Common
    private SIStatusType siStatus = SIStatusType.Unknown;
    private int masterVolume = 0;
    private boolean isMuted = false;
    private PlayStatusType playStatus = PlayStatusType.Unknown;
    private DeviceStatusType deviceStatus = DeviceStatusType.Connecting;
    public CeolDeviceNetServer NetServer;
    public CeolDeviceTuner Tuner;
    private long appStartedMsecs;

    private CeolDevice() {
        NetServer = new CeolDeviceNetServer();
        Tuner = new CeolDeviceTuner();
        appStartedMsecs = System.currentTimeMillis();
    }

    public DeviceStatusType getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(DeviceStatusType deviceStatus) {
        //Log.d(TAG, "DeviceStatus: " + deviceStatus);
        this.deviceStatus = deviceStatus;
    }

    private long deviceOnTimeMsecs = -1;

    public void setDeviceStatus(String powerString) {
        boolean deviceSaysOn = powerString.equals("ON");
        long now = System.currentTimeMillis();
        if (deviceSaysOn) {
            switch (this.deviceStatus) {
                case Connecting:
                    // We don't know how long the device has been on, just assume long enough
                    this.deviceStatus = DeviceStatusType.On;
                    deviceOnTimeMsecs = now - wakeUpPeriodMsecs;
                    break;
                case Standby:
                    // We are switching on. Start a timer before we try to communicate
                    deviceOnTimeMsecs = now;
                    this.deviceStatus = DeviceStatusType.Starting;
                    break;
                case Starting:
                    if ((now - deviceOnTimeMsecs) > wakeUpPeriodMsecs) {
                        this.deviceStatus = DeviceStatusType.On;
                    }
                    break;
                case On:
                    break;
            }
        } else {
            this.deviceStatus = DeviceStatusType.Standby;
        }
    }

    public SIStatusType getSIStatus() {
        return siStatus;
    }

    private long netServerOnTimeMsecs = 0;

    public void setSIStatus(SIStatusType newSiStatus) {
        long now = System.currentTimeMillis();

        if ( siStatus != SIStatusType.NetServer && newSiStatus == SIStatusType.NetServer ) {
            // We are trying to switch to NetServer
            if (netServerOnTimeMsecs == 0) {
                // Start timer but don't change setting
                netServerOnTimeMsecs = now;
            } else {
                if ((now - netServerOnTimeMsecs) > netServerOnPeriodMsecs) {
                    // We've waited long enough
                    this.siStatus = newSiStatus;
                    netServerOnTimeMsecs = 0;
                }
            }
        } else {
            this.siStatus = newSiStatus;
            netServerOnTimeMsecs = 0;
        }
    }

    public int getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(int masterVolume) {
        //Log.d(TAG, "setMasterVolume: " + masterVolume);
        this.masterVolume = masterVolume;
    }

    public void setMasterVolume(String masterVolume) {

        //Log.d(TAG, "setMasterVolume: " + masterVolume);
        try {
            this.masterVolume = Integer.valueOf(masterVolume);
        } catch (NumberFormatException e) {
            Log.e(TAG, "setMasterVolume: Bad volume number: " + masterVolume);
        }
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setIsMuted(boolean isMuted) {
        //Log.d(TAG, "setIsMuted: " + isMuted);
        this.isMuted = isMuted;
    }

    public PlayStatusType getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(PlayStatusType playStatus) {
        this.playStatus = playStatus;
    }

    public void setPlayStatus(String playStatusString) {
        if ( playStatusString != null) {
            switch (playStatusString.toUpperCase()) {
                case "PLAY":
                    this.playStatus = PlayStatusType.Play;
                    return;
                case "PAUSE":
                    this.playStatus = PlayStatusType.Pause;
                    return;
                case "STOP":
                    this.playStatus = PlayStatusType.Stop;
                    return;
                default:
                    this.playStatus = PlayStatusType.Unknown;
                    return;
            }
        }
    }


}
