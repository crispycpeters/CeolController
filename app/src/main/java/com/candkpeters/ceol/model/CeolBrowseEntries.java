package com.candkpeters.ceol.model;

/**
 * Created by crisp on 06/01/2016.
 *
 * "List" is the full list of entries in a given folder
 *      Ceol provides:
 *          listMax - Number of entries in entire list
 *          listPosition - The position in the entire list (starting from 1) of the currently-selected entry
 *
 * "Chunk" is the moving chunk of entries. It is all that is returned from CEOL at any given time.
 *      Ceol provides:
 *          MAX_LINES - Number of entries in the chunk (7 rows)
 *          One of the entries is marked as "selected"
 *          chunkStartIdx - Index of start of chunk
 */
public class CeolBrowseEntries {

    private static final String TAG = "CeolBrowseEntries";

    private String title;
    private String scridValue;
    private String scrid;
    private int listMax = 0;
    private int listPosition = -1;
    private int chunkStartIdx = -1;
    private int selectedEntryIndex = -1;

    enum BrowseEntryType {
        Playable,
        Directory,
    }
    public static final int MAX_LINES = 7;

    private final CeolBrowseEntry[] browseChunk = new CeolBrowseEntry[MAX_LINES];

    public CeolBrowseEntries() {
        title = "";
        scrid = "";
        scridValue = "";
        clear();
    }

    public void initialiseChunk(String title, String scridValue, String scrid, String listmax, String listposition) {
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
        chunkStartIdx = -1;
    }

    public int getListPosition() {
        return listPosition;
    }

    public int getListStartOffset() {
        if ( selectedEntryIndex != -1) {
            return listPosition - selectedEntryIndex - 1;
        } else {
            return 0;
        }
    }

    public int getSelectedEntryIndex() {
        return selectedEntryIndex;
    }

    public CeolBrowseEntry getSelectedEntry() {
        if ( selectedEntryIndex != -1 ) {
            return browseChunk[selectedEntryIndex];
        } else {
            return null;
        }
    }

    public String getBrowseLineText( int row) {
        if ( row >=0 && row < MAX_LINES) {
            return browseChunk[row] != null ? browseChunk[row].Text : "";
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
            browseChunk[i] = null;
        }
        selectedEntryIndex = -1;
    }

    public void setChunkLine(int lineIdx, String text, String attributes) {
        if ( lineIdx >= 0 && lineIdx < MAX_LINES ) {
            if ( text==null || attributes==null ) {
                browseChunk[lineIdx] = null;
            } else {
                browseChunk[lineIdx] = new CeolBrowseEntry(text, attributes);
                if (browseChunk[lineIdx].isSelected) {
                    chunkStartIdx = listPosition - lineIdx - 1;
                    selectedEntryIndex = lineIdx;
                }
                //Log.d(TAG, "setChunkLine: On line " + line + "("+attributes+"): " + browseChunk[line]);
            }
        }
    }

    public CeolBrowseEntry[] getBrowseChunk() {
        return browseChunk;
    }

    public int getChunkStartIndex() {
        return chunkStartIdx;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object object) {

        boolean isEqual = false;

        if (object instanceof CeolBrowseEntries) {
            CeolBrowseEntries ceolBrowseEntries = (CeolBrowseEntries) object;
            if (title.equals(ceolBrowseEntries.title) &&
                    scrid.equals(ceolBrowseEntries.scrid) &&
                    scridValue.equals(ceolBrowseEntries.scridValue) &&
                    listMax == ceolBrowseEntries.listMax &&
                    listPosition == ceolBrowseEntries.listPosition &&
                    chunkStartIdx == ceolBrowseEntries.chunkStartIdx &&
                    selectedEntryIndex == ceolBrowseEntries.selectedEntryIndex &&
                    entriesEqual(ceolBrowseEntries))
                isEqual = true;
        }
        return isEqual;
    }

    private boolean entriesEqual(CeolBrowseEntries ceolBrowseEntries) {
        for ( int i =0; i<MAX_LINES; i++) {
            if ( (browseChunk[i] == null && ceolBrowseEntries.browseChunk[i] != null)) {
                return false;
            }
            if ( (browseChunk[i] != null && ceolBrowseEntries.browseChunk[i] == null)) {
                return false;
            }
            if ( browseChunk[i] == null && ceolBrowseEntries.browseChunk[i] == null ) {
                return true;
            }
            if ( ! browseChunk[i].equals(ceolBrowseEntries.browseChunk[i]) ) {
                return false;
            }
        }
        return true;
    }
}
