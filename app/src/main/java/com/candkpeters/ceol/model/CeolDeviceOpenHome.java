package com.candkpeters.ceol.model;

import android.util.Log;

import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.Item;

import java.util.List;

/**
 * Created by crisp on 16/04/2017.
 */

public class CeolDeviceOpenHome {
    private static final String TAG = "CeolDeviceOpenHome";
    private final AudioItem audioItem;
    private long trackCount;
    private long duration;
    private long seconds;
    private String uri;
    private String metadataString;
    private DIDLContent didlContent;

    public CeolDeviceOpenHome(AudioItem audioItem) {
        this.audioItem = audioItem;
    }

    public boolean isOperating( ) {
        if (trackCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setTrackCount(long trackCount) {
        this.trackCount = trackCount;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setMetadata(String metadata) {
        this.metadataString = metadata;

        if ( metadata != null && metadata.length() > 0 ) {
            System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
            DIDLParser didlParser = new DIDLParser();

            try {
                audioItem.clear();
                didlContent = didlParser.parse(metadataString);
                if ( didlContent.getItems().size() > 1 ) {
                    throw new Exception("Should only have one item");
                };
                Item item = didlContent.getItems().get(0);
                audioItem.setTrack(item.getTitle() );
                audioItem.setArtist(item.getFirstPropertyValue(DIDLObject.Property.UPNP.ARTIST.class).getName() );
                audioItem.setAlbum(item.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM.class) );
                Res res = item.getFirstResource();
                audioItem.setFormat(res.getProtocolInfo().getContentFormat());
                audioItem.setBitrate(Long.toString(res.getBitrate()/1000) + "k"); // TODO Wrong - should be using protocol info flags some way
                audioItem.setImageBitmapUri(item.getFirstProperty(DIDLObject.Property.UPNP.ALBUM_ART_URI.class).getValue());
            } catch (Exception e) {
                Log.e(TAG, "setMetadata: Bad XML in DIDL", e);
            }
        }
    }

    public void setTransportState(String value) {
        // Not sure this is needed
        switch (value) {
            case "Playing":
                break;
            case "Paused":
                break;
            case "Buffering":
                break;
            case "Stopped":
                break;
        }
    }

    public long getTrackCount() {
        return trackCount;
    }
}
