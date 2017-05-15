package com.candkpeters.ceol.model;

import android.util.Log;

/**
 * Created by crisp on 03/05/2017.
 */

public class AudioControl extends ControlBase {

    private static final String TAG = "AudioControl";
    protected int masterVolume = 0;

    public AudioControl() {
    }

    public int getMasterVolume() {
        return masterVolume;
    }

    public String getMasterVolumeString() {
        return Integer.toString(masterVolume);
    }

    @Override
    protected boolean copyFrom(ControlBase newControl) {
        boolean hasChanged = false;
        if ( newControl != null && newControl instanceof AudioControl) {
            AudioControl newAudioControl = (AudioControl)newControl;
            if (this.masterVolume != newAudioControl.masterVolume) {
                this.masterVolume = newAudioControl.masterVolume;
                hasChanged = true;
            }
        }
        return hasChanged;
    }

    public boolean updateMasterVolume(String masterVolume) {
        try {
            return updateMasterVolume(Integer.valueOf(masterVolume));
        } catch (NumberFormatException e) {
            Log.e(TAG, "setMasterVolume: Bad volume number: " + masterVolume);
            return false;
        }
    }

    public boolean updateMasterVolume(int masterVolume) {
        if ( this.masterVolume != masterVolume ) {
            this.masterVolume = masterVolume;
            return true;
        } else {
            return false;
        }
    }

    public boolean updateMasterVolumePerCent(long value) {
        if ( value <= 100 ) {
            int newVolume = (int)((value+1)/2);
            return updateMasterVolume(newVolume);
        } else {
            return false;
        }
    }

}
