package com.candkpeters.ceol.model;

import android.graphics.Bitmap;

/**
 * Created by crisp on 06/01/2016.
 */
public class CeolDeviceNetServer_DELETE {

    private static final String TAG = "CeolDeviceNetServer_DELETE";

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
    private String title;
    private boolean isBrowsing = false;
    private String scridValue;
    private String scrid;

    // Browsing mode
    private CeolBrowseEntries entries = new CeolBrowseEntries();

    // Playing mode
//    private String track;
//    private String artistView;
//    private String album;
//    private String format;
//    private String bitrate;
//
//    private Bitmap imageBitmap;

    public CeolDeviceNetServer_DELETE(AudioStreamItem audioItem) {
        clear();
    }

    public void clear() {
        entries.clear();
//        setStreamInfo("","","","","");
        scrid = "";
        scridValue = "";
    }

    public void setChunkLine(int lineIdx, String text, String attributes) {
        entries.setChunkLine(lineIdx, text, attributes);
    }

    public void initialiseChunk(String title, String scridValue, String scrid, String listmax, String listposition) {
        setIsBrowsing(true);
        entries.initialiseChunk(title, scridValue, scrid, listmax, listposition);
        this.title = title;
    }

    public int getSelectedPosition() {
        return entries.getListPosition();
    }

    public CeolBrowseEntry getSelectedEntry() {
        return entries.getSelectedEntry();
    }

//    public void setStreamInfo(String track, String artistView, String album, String format, String bitrate) {
//        this.track = track;
//        this.artistView = artistView;
//        this.album = album;
//        this.format = format;
//        this.bitrate = bitrate;
//    }

    public int selectedPosition() {
        return entries.getListPosition();
    }

    public CeolBrowseEntries getEntries() {
        return entries;
    }

    public boolean isBrowsing() {
        return isBrowsing;
    }

    public void setIsBrowsing(boolean isBrowsing) {
        //Log.d(TAG, "setIsBrowsing: " + isBrowsing);
        this.isBrowsing = isBrowsing;
    }

//    public String getTitle() {
//        return track;
//    }

//    public String getArtist() {
//        return artistView;
//    }

//    public String getAlbum() {
//        return album;
//    }

    public String getScridValue() {
        return entries.getScridValue();
    }

//    public String getTitle() {
//        return titleView;
//    }

//    public String getBitrate() {
//        return bitrate == null? "": bitrate;
//    }

//    public String getFormat() {
//        return format == null? "": format;
//    }
}
