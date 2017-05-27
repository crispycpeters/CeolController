package com.candkpeters.ceol.model.control;

import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.ObservedControlType;
import com.candkpeters.ceol.model.PlayStatusType;

/**
 * Created by crisp on 03/05/2017.
 */

public class TrackControl extends ControlBase {

    private static final String TAG = "TrackControl";

    protected AudioStreamItem audioItem;
    protected PlayStatusType playStatus = PlayStatusType.Unknown;

    public TrackControl() {
        super(ObservedControlType.Track);
        audioItem = new AudioStreamItem();
    }

    public PlayStatusType getPlayStatus() {
        return playStatus;
    }

    @Override
    public boolean copyFrom(ControlBase newControl) {
        boolean hasChanged = false;
        if (newControl != null &&  newControl instanceof TrackControl) {
            TrackControl newTrackControl = (TrackControl)newControl;
            if (!this.audioItem.equals(newTrackControl)) {
                this.audioItem = newTrackControl.getAudioItem();
                hasChanged = true;
            }
            if (this.playStatus != newTrackControl.playStatus) {
                this.playStatus = newTrackControl.playStatus;
                hasChanged = true;
            }
        }
        return hasChanged;
    }

    public boolean updateAudioItem(AudioStreamItem newAudioItem) {
        boolean hasChanged = false;
        if ( newAudioItem != null ) {
            if ( !newAudioItem.equals(audioItem)) {
                hasChanged = true;
                this.audioItem = newAudioItem;
            }
        } else {
            if ( audioItem != null ) {
                hasChanged = true;
                this.audioItem = null;
            }
        }
        return hasChanged;
    }

    public boolean updatePlayStatus(PlayStatusType playStatus) {

        boolean result = false;

        if ( this.playStatus != playStatus) {
            this.playStatus = playStatus;
            result = true;
        }

        return result;
    }


    public AudioStreamItem getAudioItem() {
        return audioItem;
    }

}
