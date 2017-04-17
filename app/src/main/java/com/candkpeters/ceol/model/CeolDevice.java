package com.candkpeters.ceol.model;

import android.util.Log;

/**
 * Created by crisp on 06/01/2016.
 */
public class CeolDevice {

    private static final String TAG = "CeolDevice";
    private static final long DEFAULT_WAKEUP_PERIOD_MSECS = 3000;  // Give machine a chance to wake up before sending commands
    private static final long DEFAULT_NETSERVERON_PERIOD_MSECS = 6000; // Give NetServer a chance to settle down be believing its settings

    private static final long MAXVOLUME = 60;
    private static final long wakeUpPeriodMsecs = DEFAULT_WAKEUP_PERIOD_MSECS;
    private boolean isNetServer;

    // Common
    private SIStatusType siStatus = SIStatusType.NotConnected;
    private int masterVolume = 0;
    private boolean isMuted = false;
    private PlayStatusType playStatus = PlayStatusType.Unknown;
    private DeviceStatusType deviceStatus = DeviceStatusType.Connecting;

    // Sub-devices
    public CeolDeviceNetServer NetServer;
    public CeolDeviceTuner Tuner;
    public CeolDeviceOpenHome OpenHome;

    private long appStartedMsecs;
    private static final int REPEATRATE_MSECS = 900;
    private static final int REPEATRATE_MSECS_SPOTIFY = 8000;

    public CeolDevice() {
        NetServer = new CeolDeviceNetServer();
        Tuner = new CeolDeviceTuner();
        OpenHome = new CeolDeviceOpenHome();
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
                    setDeviceStatus(DeviceStatusType.On);
                    deviceOnTimeMsecs = now - wakeUpPeriodMsecs;
                    break;
                case Standby:
                    // We are switching on. Start a timer before we try to communicate
                    deviceOnTimeMsecs = now;
                    setDeviceStatus(DeviceStatusType.Starting);
                    break;
                case Starting:
                    if ((now - deviceOnTimeMsecs) > wakeUpPeriodMsecs) {
                        setDeviceStatus(DeviceStatusType.On);
                    }
                    break;
                case On:
                    break;
            }
        } else {
            setDeviceStatus(DeviceStatusType.Standby);
        }
    }

    public SIStatusType setSIStatus(String source) {
        SIStatusType siStatusNew;

        if ( source != null && !source.isEmpty()) {
            switch (source) {
                case "Music Server":
                    siStatusNew = SIStatusType.NetServer;
                    if ( OpenHome.isOperating() ) {
                        siStatusNew = SIStatusType.OpenHome;
                    }
                    break;
                case "TUNER":
                    siStatusNew = SIStatusType.Tuner;
                    break;
                case "CD":
                    siStatusNew = SIStatusType.CD;
                    break;
                case "Internet Radio":
                    siStatusNew = SIStatusType.IRadio;
                    break;
                case "USB":
                    siStatusNew = SIStatusType.Ipod;
                    break;
                case "ANALOGIN":
                    siStatusNew = SIStatusType.AnalogIn;
                    break;
                case "DIGITALIN1":
                    siStatusNew = SIStatusType.DigitalIn1;
                    break;
                case "DIGITALIN2":
                    siStatusNew = SIStatusType.DigitalIn2;
                    break;
                case "BLUETOOTH":
                    siStatusNew = SIStatusType.Bluetooth;
                    break;
                case "SpotifyConnect":
                    siStatusNew = SIStatusType.Spotify;
                    break;
                default:
                    siStatusNew = siStatus;
                    break;
            }
            setSIStatus(siStatusNew);
        }

        return siStatus;
    }


    public SIStatusType setSIStatusLite(String inputFunc) {
        SIStatusType siStatusNew;

        if ( inputFunc != null) {
            switch (inputFunc) {
                case "NET":
                    if (!isNetServer()) {
                        // Not sure yet what the type is yet - assume NetServer
                        isNetServer = true;
                        siStatusNew = SIStatusType.NetServer;
                    } else {
                        siStatusNew = siStatus;
                    }
                    break;
                case "TUNER":
                    isNetServer = false;
                    siStatusNew = SIStatusType.Tuner;
                    break;
                case "CD":
                    isNetServer = false;
                    siStatusNew = SIStatusType.CD;
                    break;
                case "USB":
                    isNetServer = false;
                    siStatusNew = SIStatusType.Ipod;
                    break;
                case "ANALOGIN":
                    isNetServer = false;
                    siStatusNew = SIStatusType.AnalogIn;
                    break;
                case "DIGITALIN1":
                    isNetServer = false;
                    siStatusNew = SIStatusType.DigitalIn1;
                    break;
                case "DIGITALIN2":
                    isNetServer = false;
                    siStatusNew = SIStatusType.DigitalIn2;
                    break;
                default:
                    siStatusNew = siStatus;
                    break;
            }
            setSIStatus(siStatusNew);
        }
        return siStatus;
    }

    public SIStatusType getSIStatus() {
        return siStatus;
    }

    private long netServerOnTimeMsecs = 0;

    public synchronized void setSIStatus(SIStatusType newSiStatus) {
//        long now = System.currentTimeMillis();

        this.siStatus = newSiStatus;

/*
        if ( newSiStatus == SIStatusType.NetServer &&
                (siStatus != SIStatusType.NotConnected && siStatus != SIStatusType.NetServer) ) {
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
*/

        switch (siStatus) {

            case AnalogIn:
            case CD:
            case Tuner:
                isNetServer = false;
                break;
            case NotConnected:
            case DigitalIn1:
            case DigitalIn2:
            case Bluetooth:
            case IRadio:
            case NetServer:
            case Ipod:
            case Spotify:
            case OpenHome:
            default:
                isNetServer = true;
                break;
        }
    }

    public int getMasterVolume() {
        return masterVolume;
    }

    public String getMasterVolumeString() {
        return Integer.toString(masterVolume);
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

    public void setMasterVolumePerCent(long value) {
        if ( value <= 100 ) {
           int newVolume = (int)((value+1)/2);
           Log.d(TAG, "setMasterVolumePerCent: " + newVolume);
           this.masterVolume = newVolume;
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
                    this.playStatus = PlayStatusType.Playing;
                    return;
                case "PAUSE":
                    this.playStatus = PlayStatusType.Paused;
                    return;
                case "STOP":
                    this.playStatus = PlayStatusType.Stopped;
                    return;
                default:
                    this.playStatus = PlayStatusType.Unknown;
            }
        }
    }


    public boolean isNetServer() {
        return isNetServer;
    }

    public synchronized boolean isOpenHome() {
        return OpenHome.isOperating();
//        return false;
    }

}
