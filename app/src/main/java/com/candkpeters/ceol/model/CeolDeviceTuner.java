package com.candkpeters.ceol.model;

import android.util.Log;

/**
 * Created by crisp on 06/01/2016.
 */
public class CeolDeviceTuner {

    private static final String TAG = "CeolDeviceTuner";

    private String Band = "";
    private String Frequency = "";
    private String Name = "";
    private boolean IsAuto = false;

    public CeolDeviceTuner() {
    }

    public String getBand() {
        return Band;
    }

    public void setBand(String band) {
        Band = band;
    }

    public String getFrequency() {
        return Frequency;
    }

    public void setFrequency(String frequency) {
        Frequency = frequency;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isAuto() {
        return IsAuto;
    }

    public void setIsAuto(boolean isAuto) {
        IsAuto = isAuto;
    }
}
