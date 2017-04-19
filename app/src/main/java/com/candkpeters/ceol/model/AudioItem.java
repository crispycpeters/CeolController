package com.candkpeters.ceol.model;

import android.graphics.Bitmap;

import org.fourthline.cling.support.model.DIDLObject;

import java.net.URI;

/**
 * Created by crisp on 06/01/2016.
 */
public class AudioItem {

    private static final String TAG = "AudioItem";

    // Common
    private String track;
    private String artist;
    private String album;
    private String format;
    private String bitrate;
    private String audioUrl;
    private URI imageBitmapUri;
    private Bitmap imageBitmap;

    public AudioItem() {
        clear();
    }

    public void clear() {
        setTrackInfo("","","","","");
        imageBitmap = null;
        imageBitmapUri = null;
    }

    public void setTrackInfo(String track, String artist, String album, String format, String bitrate) {
        this.track = track;
        this.artist = artist;
        this.album = album;
        this.format = format;
        this.bitrate = bitrate;
    }

    public String getTrack() {
        return track;
    }
    public void setTrack(String track) {
        this.track = track;
    }

    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }
    public void setAlbum(String album) {
        this.album= album;
    }

    public String getBitrate() {
        return bitrate == null? "": bitrate;
    }
    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public String getFormat() {
        return format == null? "": format;
    }
    public void setFormat(String format) {
        this.format = format;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }
    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public URI getImageBitmapUri() {
        return imageBitmapUri;
    }
    public void setImageBitmapUri(URI imageBitmap) {
        this.imageBitmap = null;
        this.imageBitmapUri = imageBitmap;
    }
}
