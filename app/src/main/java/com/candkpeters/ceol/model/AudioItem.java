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
    private int id;

    public AudioItem() {
        clear();
    }

    @Override
    public String toString() {
        return "AudioItem: id="+id +" Track="+track;
    }

    public AudioItem(int id) {
        clear();
        this.id = id;
    }

    public void clear() {
        setTrackInfo(0, "","","","","");
        imageBitmapUri = null;
        audioUrl = null;
    }

    public void setTrackInfo(int id, String track, String artist, String album, String format, String bitrate) {
        this.id = 0;
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
        this.imageBitmapUri = imageBitmap;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPoopulated() {
        return track!=null && track.length()>0;
    }

    public void setAudioItem(AudioItem audioItem) {
        id = audioItem.id;
        track = audioItem.track;
        artist = audioItem.artist;
        album = audioItem.album;
        bitrate = audioItem.bitrate;
        format = audioItem.format;
        if ( audioItem.imageBitmap != null ) {
            imageBitmap = audioItem.imageBitmap;
        }
        imageBitmapUri = audioItem.imageBitmapUri;
        audioUrl = audioItem.audioUrl;
    }
}
