package com.candkpeters.ceol.model;

/**
 * Created by crisp on 06/01/2016.
 */
public class CeolDeviceTuner_DELETE {

    private static final String TAG = "CeolDeviceTuner_DELETE";

    private String Band = "";
    private String Frequency = "";
    private String Name = "";
    private boolean IsAuto = false;

    public CeolDeviceTuner_DELETE() {
    }

    public String getBand() {
        return Band;
    }

    public String getUnits() {
        if ( getBand().equalsIgnoreCase("FM")) {
            return "MHz";
        } else {
            return "kHz";
        }
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
