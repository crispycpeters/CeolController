package com.candkpeters.ceol.model;

/**
 * Created by crisp on 03/05/2017.
 */

public class InputControl extends ControlBase {

    private static final String TAG = "AudioControl";

    protected SIStatusType siStatus = SIStatusType.NotConnected;
    protected StreamingStatus streamingStatus = StreamingStatus.CEOL;
    final public TrackControl trackControl = new TrackControl();
    public PlaylistControlBase playlistControl;
    public CeolNavigatorControl navigatorControl;   // TODO - Will need to be refactored for other navigators

    public SIStatusType getSIStatus() {
        return siStatus;
    }

    public StreamingStatus getStreamingStatus() {
        return streamingStatus;
    }

    public boolean updatePlaylistControl(PlaylistControlBase playlistControl) {
        boolean hasChanged = false;

        if (this.playlistControl != null) {
            if (playlistControl == null) {
                hasChanged = true;
            } else {
                if (!this.playlistControl.equals(playlistControl)) {
                    this.playlistControl = playlistControl;
                    hasChanged = true;
                }
            }
        } else {
            if (playlistControl != null) {
                this.playlistControl = playlistControl;
                hasChanged = true;
            }
        }
        return hasChanged;
    }

    public boolean updateNavigatorControl(CeolNavigatorControl navigatorControl) {
        boolean hasChanged = false;

        if (this.navigatorControl != null) {
            if (navigatorControl == null) {
                hasChanged = true;
            } else {
                if (!this.navigatorControl.equals(navigatorControl)) {
                    this.navigatorControl = navigatorControl;
                    hasChanged = true;
                }
            }
        } else {
            if (navigatorControl != null) {
                this.navigatorControl = navigatorControl;
                hasChanged = true;
            }
        }
        return hasChanged;
    }

    private void checkStreamingStatus() {
        switch (streamingStatus) {
            case CEOL:
                // TODO Needs refactoring when we have other types of navigation and playlist
                if ( navigatorControl == null || !(navigatorControl instanceof CeolNavigatorControl)) {
                    updateNavigatorControl(new CeolNavigatorControl());
                }
                if ( playlistControl != null && (playlistControl instanceof PlaylistControlBase)) {
                    updateNavigatorControl(null);
                }
                break;
            case DLNA:
                break;
            case OPENHOME:
                break;
            case SPOTIFY:
                // TODO Needs refactoring when we have other types of navigation and playlist
                if ( navigatorControl != null && (navigatorControl instanceof CeolNavigatorControl)) {
                    updatePlaylistControl(null);
                }
                if ( playlistControl == null || !(playlistControl instanceof PlaylistControlBase)) {
                    updatePlaylistControl(new PlaylistControlBase());
                }
                break;
            case NONE:
                break;
        }
    }


    @Override
    public boolean copyFrom(ControlBase newControl) {
        boolean hasChanged = false;
        if (newControl != null && newControl instanceof InputControl) {
            InputControl newInputControl = (InputControl) newControl;
            if (this.siStatus != newInputControl.siStatus) {
                this.siStatus = newInputControl.siStatus;
                hasChanged = true;
            }
            if (this.streamingStatus != newInputControl.streamingStatus) {
                this.streamingStatus = newInputControl.streamingStatus;
                hasChanged = true;
            }
            if (this.trackControl.copyFrom(newInputControl.trackControl)) hasChanged = true;
            if (this.trackControl.copyFrom(newInputControl.playlistControl)) hasChanged = true;
            if (this.trackControl.copyFrom(newInputControl.navigatorControl)) hasChanged = true;
        }
        return hasChanged;
    }


    public boolean updateSIStatus(String source) {
        SIStatusType siStatusNew;

        if (source != null && !source.isEmpty()) {
            switch (source) {
                case "Music Server":
                    siStatusNew = SIStatusType.NetServer;
//                    if ( openHome.isOperating() ) {
//                        siStatusNew = SIStatusType.OpenHome;
//                    }
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
            return updateSIStatus(siStatusNew);
        } else {
            return false;
        }

//        return siStatus;
    }

    public synchronized boolean updateSIStatus(SIStatusType newSiStatus) {
        boolean hasChanged = false;

        if (this.siStatus != newSiStatus) {
            this.siStatus = newSiStatus;
            hasChanged = true;

            switch (siStatus) {

                case AnalogIn:
                case CD:
                case Tuner:
                case NotConnected:
                case DigitalIn1:
                case DigitalIn2:
                    streamingStatus = StreamingStatus.NONE;

                    break;
                case Bluetooth:
                case IRadio:
                case Ipod:
                case NetServer:
                    streamingStatus = StreamingStatus.CEOL;
                    break;
                case Spotify:
                    streamingStatus = StreamingStatus.SPOTIFY;
                    break;
                case OpenHome:
                    streamingStatus = StreamingStatus.OPENHOME;
                    break;
                default:
                    break;
            }
        }

        checkStreamingStatus();
        return hasChanged;
    }


}