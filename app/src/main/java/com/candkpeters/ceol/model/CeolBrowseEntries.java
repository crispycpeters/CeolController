package com.candkpeters.ceol.model;

import android.util.Log;

/**
 * Created by crisp on 06/01/2016.
 */
public class CeolBrowseEntries {

    private static final String TAG = "CeolBrowseEntries";

    private String title;
    private String scridValue;
    private String scrid;
    private int listMax = 0;
    private int listPosition = -1;
    private int listOffset = -1;
    private int selectedEntryIndex = -1;

    enum BrowseEntryType {
        Playable,
        Directory,
    }
    public static final int MAX_LINES = 7;

    private CeolBrowseEntry[] browseLines = new CeolBrowseEntry[MAX_LINES];

    public CeolBrowseEntries() {
        title = "";
        scrid = "";
        scridValue = "";
        clear();
    }

    public void initializeEntries(String title, String scridValue, String scrid, String listmax, String listposition) {
        this.title = title;
        int oldListMax = listMax;
        selectedEntryIndex = -1;

        clear();
        this.scrid = scrid;
        this.scridValue = scridValue;
        try {
            this.listMax = Integer.parseInt(listmax);
            this.listPosition = Integer.parseInt(listposition);
        } catch ( NumberFormatException e) {
            this.listMax = 0;
            this.listPosition = -1;
        }
        listOffset = -1;

    }

    public int getSelectedPosition() {
        return listPosition;
    }

    public int getSelectedEntryIndex() {
        return selectedEntryIndex;
    }

    public CeolBrowseEntry getSelectedEntry() {
        if ( selectedEntryIndex != -1 ) {
            return browseLines[selectedEntryIndex];
        } else {
            return null;
        }
    }

    public String getBrowseLineText( int row) {
        if ( row >=0 && row < MAX_LINES) {
            return browseLines[row] != null ? browseLines[row].Text : "";
        } else {
            return "";
        }
    }

    public int getListMax() {
        return listMax;
    }

    public String getScridValue() {
        return scridValue;
    }

    public void clear() {
        for ( int i =0; i<MAX_LINES; i++) {
            browseLines[i] = null;
        }
        selectedEntryIndex = -1;
    }

    public void setBrowseLine( int line, String text, String attributes) {
        if ( line >= 0 && line < MAX_LINES ) {
            if ( text==null || attributes==null ) {
                browseLines[line] = null;
            } else {
                browseLines[line] = new CeolBrowseEntry(text, attributes);
                if (browseLines[line].isSelected) {
                    listOffset = listPosition - line;
                    selectedEntryIndex = line;
                }
                //Log.d(TAG, "setBrowseLine: On line " + line + "("+attributes+"): " + browseLines[line]);
            }
        }
    }

    public CeolBrowseEntry[] getBrowseLines() {
        return browseLines;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
