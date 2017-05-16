package com.candkpeters.ceol.model;

/**
 * Created by crisp on 06/01/2016.
 */
public class AudioTunerItem_DELETE extends AudioItem_DELETE {

    private static final String TAG = "AudioTunerItem_DELETE";

    // Common
    private String band;
    private String frequency;
    private boolean isAuto;

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getUnits() {
        if ( getBand().equalsIgnoreCase("FM")) {
            return "MHz";
        } else {
            return "kHz";
        }
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }


    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }


    public AudioTunerItem_DELETE() {
        clear();
    }

    @Override
    public String toString() {
        return "AudioTunerItem_DELETE: title="+title +" frequency="+frequency;
    }

    public void clear() {
        band = frequency = "";
        isAuto = false;
    }

    public void setAudioItem(AudioTunerItem_DELETE audioItem) {
        title = audioItem.title;
        frequency = audioItem.frequency;
        band = audioItem.band;
        isAuto= audioItem.isAuto;
    }

    @Override
    public boolean equals( Object object) {
        boolean isEqual = false;

        if (object instanceof AudioTunerItem_DELETE) {
            AudioTunerItem_DELETE ai = (AudioTunerItem_DELETE)object;
            if (
                    title.equals(ai.title) &&
                    frequency.equals(ai.frequency) &&
                    band.equals(ai.band) &&
                    isAuto == ai.isAuto ) {
                isEqual = true;
            }
        }
        return isEqual;
    }
}
