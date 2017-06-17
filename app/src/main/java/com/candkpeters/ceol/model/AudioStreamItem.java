package com.candkpeters.ceol.model;

import android.graphics.Bitmap;
import android.util.Log;

import java.net.URL;

/**
 * Created by crisp on 06/01/2016.
 */
public class AudioStreamItem {

    private static final String TAG = "AudioStreamItem";

    // Streaming
    private String title = "";
    private String artist;
    private String album;
    private String format;
    private String bitrate;
    private String audioUrl;
    private URL imageBitmapUrl;
    private Bitmap imageBitmap;
    private int id;
    private int duration;

    // Tuner
    private String band;
    private String frequency;
    private boolean isAuto;

    public AudioStreamItem() {
        clear();
    }

    @Override
    public String toString() {
        return "AudioStreamItem: id="+id +" title="+title;
    }

    public AudioStreamItem(int id) {
        clear();
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    private boolean setTitle(String title) {
        title = title == null ? "" : title;

        if ( !this.title.equals(title)) {
            this.title = title;
            return true;
        } else {
            return true;
        }
    }

    private void clear() {
        id = 0;
        title = artist = album = format = bitrate = band = frequency = "";
        isAuto = false;
        imageBitmapUrl = null;
        audioUrl = null;
        duration = 0;
    }

    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist != null ? artist : "";
    }

    public String getAlbum() {
        return album;
    }
    public void setAlbum(String album) {
        this.album = album != null ? album : "";
    }

    public String getBitrate() {
        return bitrate;
    }
    public void setBitrate(String bitrate) {
        this.bitrate = bitrate != null ? bitrate : "";
    }

    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format != null ? format : "";
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }
    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public URL getImageBitmapUrl() {
        return imageBitmapUrl;
    }
    public void setImageBitmapUrl(URL imageBitmap) {
        this.imageBitmapUrl = imageBitmap;
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

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getUnits() {
        if ( getBand().equalsIgnoreCase("FM")) {
            return "MHz";
        } else {
            return "kHz";
        }
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }


    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }


    public void setAudioItem(AudioStreamItem audioItem) {
        id = audioItem.id;
        title = audioItem.title;
        artist = audioItem.artist;
        album = audioItem.album;
        bitrate = audioItem.bitrate;
        format = audioItem.format;
        duration = audioItem.duration;
        imageBitmap = audioItem.imageBitmap;
        imageBitmapUrl = audioItem.imageBitmapUrl;
        audioUrl = audioItem.audioUrl;
        frequency = audioItem.frequency;
        band = audioItem.band;
        isAuto= audioItem.isAuto;
    }

    private boolean safeEquals( String s1, String s2 ) {
        if ( s1 == null ) {
            return (s2 == null);
        } else {
            if ( s2 == null ) return false;
            else return s1.equals(s2);
        }
    }

    @Override
    public boolean equals( Object object) {

        boolean isEqual = false;

        if (object instanceof AudioStreamItem) {
            AudioStreamItem ai = (AudioStreamItem)object;
            if (    safeEquals(this.audioUrl, ai.audioUrl ) &&
                    title.equals(ai.title) &&
                    artist.equals(ai.artist) &&
                    album.equals(ai.album) &&
// bitrate could vary
//                    bitrate.equals(ai.bitrate) &&
                    format.equals(ai.format) &&
                    id == ai.id &&
                    duration == ai.duration  &&
                    frequency.equals(ai.frequency) &&
                    band.equals(ai.band) &&
                    isAuto == ai.isAuto ) {
                isEqual = true;
            } else {
                Log.d(TAG, "equals: False");
            }
        }
        return isEqual;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getKey() {
        return imageBitmapUrl != null ? imageBitmapUrl.toString() : ""
                + title != null ? title : ""
                + album != null ? album : "";
    }
}
