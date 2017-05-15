package com.candkpeters.ceol.model;

import android.graphics.Bitmap;

import java.net.URI;

/**
 * Created by crisp on 06/01/2016.
 */
public class AudioItem {

    private static final String TAG = "AudioItem";

    // Common
    protected String title = "";

    public AudioItem() {
        clear();
    }

    @Override
    public String toString() {
        return "AudioItem: title="+ title;
    }

    public void clear() {
        title = "";
    }

    public String getTitle() {
        return title;
    }
    public boolean setTitle(String title) {
        if ( !this.title.equals(title)) {
            this.title = title;
            return true;
        } else {
            return true;
        }
    }

    public boolean isPoopulated() {
        return title !=null && title.length()>0;
    }

    public boolean setAudioItem(AudioItem audioItem) {
        boolean hasChanged = false;
        if ( audioItem != null ) {
            if ( setTitle(audioItem.title) ) hasChanged = true;
        }
        return hasChanged;
    }

    @Override
    public boolean equals( Object object) {
        boolean isEqual = false;

        if (object instanceof AudioItem) {
            AudioItem ai = (AudioItem)object;
            if (title.equals(ai.title) ) {
                isEqual = true;
            }
        }
        return isEqual;
    }

    public boolean copyFrom(AudioItem newAudioItem) {
        boolean hasChanged = false;
        if (newAudioItem != null ) {
            hasChanged = setTitle(newAudioItem.title);
        }
        return hasChanged;
    }

}
