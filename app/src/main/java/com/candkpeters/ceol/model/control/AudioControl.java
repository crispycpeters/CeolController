package com.candkpeters.ceol.model.control;

import android.util.Log;

import com.candkpeters.ceol.model.ObservedControlType;

/**
 * Created by crisp on 03/05/2017.
 */

public class AudioControl extends ControlBase {

    private static final String TAG = "AudioControl";
    protected int masterVolume = 0;

    public AudioControl() {
        super(ObservedControlType.Audio);
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
        boolean isChanged = false;

        try {
            isChanged = updateMasterVolume(Integer.valueOf(masterVolume));
//            Log.d(TAG, "updateMasterVolume: ischanged=" + isChanged);
        } catch (NumberFormatException e) {
            Log.e(TAG, "setMasterVolume: Bad volume number: " + masterVolume);
        }
        return isChanged;
    }

    public boolean updateMasterVolume(int masterVolume) {
        boolean isChanged = false;

        if ( this.masterVolume != masterVolume ) {
            this.masterVolume = masterVolume;
            isChanged = true;
        }

        return isChanged;
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
