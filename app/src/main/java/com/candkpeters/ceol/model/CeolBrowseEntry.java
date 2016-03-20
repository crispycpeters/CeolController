package com.candkpeters.ceol.model;

/**
 * Created by crisp on 10/01/2016.
 */
public class CeolBrowseEntry {

    public boolean isPlayable = false;
    public boolean isDirectory = false;
    public boolean isServer = false;
    public boolean isSelected = false;
    public String Text = "";

    public CeolBrowseEntry( String text, boolean isDirectory, boolean isPlayable, boolean isServer, boolean isSelected) {
        this.Text = text;
        this.isDirectory = isDirectory;
        this.isPlayable = isPlayable;
        this.isServer = isServer;
        this.isSelected = isSelected;
    }

    public CeolBrowseEntry( String text, String attributes) {
        this.Text = text;
        SetAttributes(attributes);
    }

    public CeolBrowseEntry() {
    }

    public void SetAttributes( String attributes) {
        if ( attributes != null ) {
            isPlayable = (attributes.contains("p"));
            isServer = (attributes.contains("S"));
            isDirectory = (attributes.contains("d"));
            isSelected = (attributes.contains("s"));
        }
    }

    public boolean isEmpty() {
        return (!isPlayable && !isDirectory);
    }

    public String toString() {
        return "Entry(" + Text + "), s=" + isSelected + " d=" + isDirectory + " p=" + isPlayable + " S=" + isServer;
    }
}
