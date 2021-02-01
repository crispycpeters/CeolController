package com.candkpeters.ceol.model.control;

import com.candkpeters.ceol.model.CeolBrowseEntries;
import com.candkpeters.ceol.model.CeolBrowseEntry;

/**
 * Created by crisp on 03/05/2017.
 */

public class CeolNavigatorControl extends NavigatorControlBase {

    private static final String TAG = "CeolNavigatorControl";
    public int getListMax() {
        return entries.getListMax();
    }

//    public Bitmap getImageBitmap() {
//        return imageBitmap;
//    }
//
//    public void setImageBitmap(Bitmap imageBitmap) {
//        this.imageBitmap = imageBitmap;
//    }

    enum BrowseEntryType {
        Playable,
        Directory,
    }
    public static final int MAX_LINES = 7;

    // Common
    protected boolean isBrowsing = false;

    // Browsing mode
    protected final CeolBrowseEntries entries = new CeolBrowseEntries();

    // Playing mode
//    private String track;
//    private String artistView;
//    private String album;
//    private String format;
//    private String bitrate;
//
//    private Bitmap imageBitmap;

    public int getSelectedPosition() {
        return entries.getListPosition();
    }

    public CeolBrowseEntry getSelectedEntry() {
        return entries.getSelectedEntry();
    }

    public int selectedPosition() {
        return entries.getListPosition();
    }

    public CeolBrowseEntries getEntries() {
        return entries;
    }

    public boolean isBrowsing() {
        return isBrowsing;
    }

    public String getScridValue() {
        return entries.getScridValue();
    }

    public void clear() {
        entries.clear();
//        setStreamInfo("","","","","");
    }

    public void setChunkLine(int lineIdx, String text, String attributes) {
        entries.setChunkLine(lineIdx, text, attributes);
    }

    public void initialiseChunk(String title, String scridValue, String scrid, String listmax, String listposition) {
        setIsBrowsing(true);
        entries.initialiseChunk(title, scridValue, scrid, listmax, listposition);
    }

    public void setIsBrowsing(boolean isBrowsing) {
        //Log.d(TAG, "setIsBrowsing: " + isBrowsing);
        this.isBrowsing = isBrowsing;
    }

    @Override
    public boolean copyFrom(ControlBase newControl) {
        boolean hasChanged = false;
        if ( newControl != null && newControl instanceof CeolNavigatorControl) {
            CeolNavigatorControl newCeolNavigatorControl = (CeolNavigatorControl)newControl;
            //Todo - for now always copy new
//            if (this.deviceStatus != newCeolNavigatorControl.deviceStatus) {
//                this.deviceStatus = newCeolNavigatorControl.deviceStatus;
                hasChanged = true;
//            }
        }
        return hasChanged;
    }

    @Override
    public boolean equals(Object object) {

        boolean isEqual = false;

        if (object instanceof CeolNavigatorControl) {
            CeolNavigatorControl ceolNavigatorControl = (CeolNavigatorControl) object;
            if (isBrowsing == ceolNavigatorControl.isBrowsing &&
                    entries.equals(ceolNavigatorControl.entries)) {
                isEqual = true;
            }
        }
        return isEqual;
    }
}
