package com.candkpeters.ceol.model;

import android.graphics.Bitmap;

/**
 * Created by crisp on 06/01/2016.
 */
public class CeolDeviceNetServer {

    private static final String TAG = "CeolDeviceNetServer";

    public int getListMax() {
        return entries.getListMax();
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

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
    private String track;
    private String artist;
    private String album;
    private String format;
    private String bitrate;

    private Bitmap imageBitmap;

    public CeolDeviceNetServer() {
        clear();
    }

    public void clear() {
        entries.clear();
        setTrackInfo("","","","","");
        scrid = "";
        scridValue = "";
    }

    public void setBrowseLine( int line, String text, String attributes) {
        entries.setBrowseLine(line, text, attributes);
    }

    public void initializeEntries(String title, String scridValue, String scrid, String listmax, String listposition) {
        setIsBrowsing(true);
        entries.initializeEntries(title, scridValue, scrid, listmax, listposition);
        this.title = title;
    }

    public int getSelectedPosition() {
        return entries.getSelectedPosition();
    }

    public CeolBrowseEntry getSelectedEntry() {
        return entries.getSelectedEntry();
    }

    public void setTrackInfo(String track, String artist, String album, String format, String bitrate) {
        this.track = track;
        this.artist = artist;
        this.album = album;
        this.format = format;
        this.bitrate = bitrate;
    }

    public int selectedPosition() {
        return entries.getSelectedPosition();
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

    public String getTrack() {
        return track;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getScridValue() {
        return entries.getScridValue();
    }

    public String getTitle() {
        return title;
    }

    public String getBitrate() {
        return bitrate;
    }

    public String getFormat() {
        return format;
    }
}
