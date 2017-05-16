package com.candkpeters.ceol.model;

import android.net.rtp.AudioStream;
import android.util.Log;

import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.Item;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.net.URI;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

/**
 * Created by crisp on 16/04/2017.
 */

public class CeolDeviceOpenHome_DELETE {
    private static final String TAG = "CeolDeviceOpenHome_DELETE";
    private static final String TESTIT = "<TrackList><Entry><Id>1</Id><Uri>http://192.168.0.2:9790/minimserver/*/Music/_Pop/Maximo*20Park/Missing*20Songs/03*20My*20Life*20In*20Reverse.mp3</Uri><Metadata>&lt;DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\" xmlns:sec=\"http://www.sec.co.kr/\" xmlns:pv=\"http://www.pv.com/pvns/\"&gt;&lt;item id=\"0$playlists$*p2$*i24894\" parentID=\"0$playlists$*p2\" restricted=\"1\"&gt;&lt;upnp:class&gt;object.item.audioItem.musicTrack&lt;/upnp:class&gt;&lt;dc:titleView&gt;My Life In Reverse&lt;/dc:titleView&gt;&lt;dc:creator&gt;Maximo Park&lt;/dc:creator&gt;&lt;upnp:artistView&gt;Maximo Park&lt;/upnp:artistView&gt;&lt;upnp:albumArtURI&gt;http://192.168.0.2:9790/minimserver/*/Music/_Pop/Maximo*20Park/Missing*20Songs/03*20My*20Life*20In*20Reverse.mp3/$!picture-420-20469.jpg&lt;/upnp:albumArtURI&gt;&lt;upnp:genre&gt;Rock&lt;/upnp:genre&gt;&lt;dc:date&gt;2005-01-01&lt;/dc:date&gt;&lt;upnp:album&gt;Missing Songs&lt;/upnp:album&gt;&lt;upnp:originalTrackNumber&gt;3&lt;/upnp:originalTrackNumber&gt;&lt;ownerUdn&gt;a8b32060-edd9-492d-8b92-9ca0e2a9764e&lt;/ownerUdn&gt;&lt;res protocolInfo=\"http-get:*:audio/mpeg:DLNA.ORG_PN=MP3;DLNA.ORG_OP=01;DLNA.ORG_FLAGS=01700000000000000000000000000000\" bitrate=\"24000\" sampleFrequency=\"44100\" nrAudioChannels=\"2\" size=\"4845479\" duration=\"0:03:20.000\"&gt;http://192.168.0.2:9790/minimserver/*/Music/_Pop/Maximo*20Park/Missing*20Songs/03*20My*20Life*20In*20Reverse.mp3&lt;/res&gt;&lt;/item&gt;&lt;/DIDL-Lite&gt;</Metadata></Entry><Entry><Id>2</Id><Uri>http://192.168.0.2:9790/minimserver/*/Music/_Pop/Ray*20Charles/Six*20Pack/Mess*20Around.mp3</Uri><Metadata>&lt;DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\" xmlns:sec=\"http://www.sec.co.kr/\" xmlns:pv=\"http://www.pv.com/pvns/\"&gt;&lt;item id=\"0$playlists$*p2$*i23543\" parentID=\"0$playlists$*p2\" restricted=\"1\"&gt;&lt;upnp:class&gt;object.item.audioItem.musicTrack&lt;/upnp:class&gt;&lt;dc:titleView&gt;Mess Around&lt;/dc:titleView&gt;&lt;dc:creator&gt;Ray Charles&lt;/dc:creator&gt;&lt;upnp:artistView&gt;Ray Charles&lt;/upnp:artistView&gt;&lt;upnp:albumArtURI&gt;http://192.168.0.2:9790/minimserver/*/Music/_Pop/Ray*20Charles/Six*20Pack/Mess*20Around.mp3/$!picture-178-19054.jpg&lt;/upnp:albumArtURI&gt;&lt;upnp:genre&gt;0&lt;/upnp:genre&gt;&lt;upnp:album&gt;Six Pack&lt;/upnp:album&gt;&lt;upnp:originalTrackNumber&gt;7&lt;/upnp:originalTrackNumber&gt;&lt;ownerUdn&gt;a8b32060-edd9-492d-8b92-9ca0e2a9764e&lt;/ownerUdn&gt;&lt;res protocolInfo=\"http-get:*:audio/mpeg:DLNA.ORG_PN=MP3;DLNA.ORG_OP=01;DLNA.ORG_FLAGS=01700000000000000000000000000000\" bitrate=\"20160\" sampleFrequency=\"44100\" nrAudioChannels=\"2\" size=\"3219645\" duration=\"0:02:38.000\"&gt;http://192.168.0.2:9790/minimserver/*/Music/_Pop/Ray*20Charles/Six*20Pack/Mess*20Around.mp3&lt;/res&gt;&lt;/item&gt;&lt;/DIDL-Lite&gt;</Metadata></Entry><Entry><Id>3</Id><Uri>http://192.168.0.2:9790/minimserver/*/Music/_Pop/Various/paul/8*20-*20Sin*20ella.mp3</Uri><Metadata>&lt;DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\" xmlns:sec=\"http://www.sec.co.kr/\" xmlns:pv=\"http://www.pv.com/pvns/\"&gt;&lt;item id=\"0$playlists$*p2$*i32566\" parentID=\"0$playlists$*p2\" restricted=\"1\"&gt;&lt;upnp:class&gt;object.item.audioItem.musicTrack&lt;/upnp:class&gt;&lt;dc:titleView&gt;Sin ella&lt;/dc:titleView&gt;&lt;dc:creator&gt;Gipsy Kings&lt;/dc:creator&gt;&lt;upnp:artistView&gt;Gipsy Kings&lt;/upnp:artistView&gt;&lt;upnp:albumArtURI&gt;http://192.168.0.2:9790/minimserver/*/Music/_Pop/Various/paul/cover.jpg&lt;/upnp:albumArtURI&gt;&lt;dc:date&gt;1992-01-01&lt;/dc:date&gt;&lt;upnp:album&gt;Gipsy Kings Live&lt;/upnp:album&gt;&lt;upnp:originalTrackNumber&gt;8&lt;/upnp:originalTrackNumber&gt;&lt;ownerUdn&gt;a8b32060-edd9-492d-8b92-9ca0e2a9764e&lt;/ownerUdn&gt;&lt;res protocolInfo=\"http-get:*:audio/mpeg:DLNA.ORG_PN=MP3;DLNA.ORG_OP=01;DLNA.ORG_FLAGS=01700000000000000000000000000000\" bitrate=\"16000\" sampleFrequency=\"44100\" nrAudioChannels=\"2\" size=\"4388733\" duration=\"0:04:34.000\"&gt;http://192.168.0.2:9790/minimserver/*/Music/_Pop/Various/paul/8*20-*20Sin*20ella.mp3&lt;/res&gt;&lt;/item&gt;&lt;/DIDL-Lite&gt;</Metadata></Entry><Entry><Id>4</Id><Uri>http://192.168.0.2:9790/minimserver/*/Music/_Pop/Audioslave/Audioslave/12*20Light*20My*20Way.mp3</Uri><Metadata>&lt;DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\" xmlns:sec=\"http://www.sec.co.kr/\" xmlns:pv=\"http://www.pv.com/pvns/\"&gt;&lt;item id=\"0$playlists$*p2$*i21243\" parentID=\"0$playlists$*p2\" restricted=\"1\"&gt;&lt;upnp:class&gt;object.item.audioItem.musicTrack&lt;/upnp:class&gt;&lt;dc:titleView&gt;Light My Way&lt;/dc:titleView&gt;&lt;dc:creator&gt;Audioslave&lt;/dc:creator&gt;&lt;upnp:artistView&gt;Audioslave&lt;/upnp:artistView&gt;&lt;upnp:artistView role=\"Composer\"&gt;Audioslave, Chris Cornell&lt;/upnp:artistView&gt;&lt;upnp:albumArtURI&gt;http://192.168.0.2:9790/minimserver/*/Music/_Pop/Audioslave/Audioslave/12*20Light*20My*20Way.mp3/$!picture-574-52744.jpg&lt;/upnp:albumArtURI&gt;&lt;upnp:genre&gt;Metal&lt;/upnp:genre&gt;&lt;dc:date&gt;2002-01-01&lt;/dc:date&gt;&lt;upnp:album&gt;Audioslave&lt;/upnp:album&gt;&lt;upnp:originalTrackNumber&gt;12&lt;/upnp:originalTrackNumber&gt;&lt;ownerUdn&gt;a8b32060-edd9-492d-8b92-9ca0e2a9764e&lt;/ownerUdn&gt;&lt;res protocolInfo=\"http-get:*:audio/mpeg:DLNA.ORG_PN=MP3;DLNA.ORG_OP=01;DLNA.ORG_FLAGS=01700000000000000000000000000000\" bitrate=\"20000\" sampleFrequency=\"44100\" nrAudioChannels=\"2\" size=\"6128441\" duration=\"0:05:03.000\"&gt;http://192.168.0.2:9790/minimserver/*/Music/_Pop/Audioslave/Audioslave/12*20Light*20My*20Way.mp3&lt;/res&gt;&lt;/item&gt;&lt;/DIDL-Lite&gt;</Metadata></Entry></TrackList>";
    private final AudioStreamItem currentAudioItem;
    private long totalTrackCount;
    private long duration;
    private long seconds;
    private String audioUri;
    private String metadataString;
    private DIDLContent didlContent;
    private int[] playlist;
    private int[] TESTPLAYLIST = {
      1,2,3,4
    };
    private Hashtable<Integer, AudioStreamItem> audioList;

    public CeolDeviceOpenHome_DELETE(AudioStreamItem currentAudioItem) {
        this.currentAudioItem = currentAudioItem;
        audioList = new Hashtable<Integer, AudioStreamItem>();

//        parseReadList(TESTIT);
//        setPlaylist( TESTPLAYLIST);
    }

    public boolean isOperating( ) {
        if (totalTrackCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setTotalTrackCount(long totalTrackCount) {
        this.totalTrackCount = totalTrackCount;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }

    public void setMetadata(String metadata) {
        this.metadataString = metadata;

        AudioStreamItem audioItem = parseDIDL( metadata);
        if ( audioItem != null) {
            currentAudioItem.setAudioItem( audioItem);
        }
    }

    public int getPlaylistLen() {
        if ( playlist != null) {
            return playlist.length;
        } else {
            return 0;
        }
    }

    public AudioStreamItem getPlaylistAudioItem(int pos) {
        if ( playlist != null && playlist.length > pos) {
            return findAudioItemById(playlist[pos]);
        } else {
            return null;
        }
    }

    private AudioStreamItem parseDIDL(String metadata) {
        AudioStreamItem audioItem = new AudioStreamItem();
        if ( metadata != null && metadata.length() > 0 ) {
            System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
            DIDLParser didlParser = new DIDLParser();

            try {
                audioItem.clear();
                Log.d(TAG, "parseDIDL: Parsing: "+ metadata);
                didlContent = didlParser.parse(metadata);
                if ( didlContent.getItems().size() > 1 ) {
                    throw new Exception("Should only have one item");
                };
                Item item = didlContent.getItems().get(0);
                audioItem.setTitle(item.getTitle() );
                audioItem.setArtist(item.getFirstPropertyValue(DIDLObject.Property.UPNP.ARTIST.class).getName() );
                audioItem.setAlbum(item.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM.class) );
                Res res = item.getFirstResource();
                audioItem.setFormat(res.getProtocolInfo().getContentFormat());
                audioItem.setBitrate(Long.toString(res.getBitrate()/1000) + "k"); // TODO Wrong - should be using protocol info flags some way
                DIDLObject.Property<URI> didlUri = item.getFirstProperty(DIDLObject.Property.UPNP.ALBUM_ART_URI.class);
                if ( didlUri != null) {
                    audioItem.setImageBitmapUri(didlUri.getValue());
                }
            } catch (Exception e) {
                Log.e(TAG, "parseDIDL: Bad XML in DIDL.", e);
            }
        }
        return audioItem;
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

    public long getTotalTrackCount() {
        return totalTrackCount;
    }

    public long getSeconds() {
        return seconds;
    }

    public long getDuration() {
        return duration;
    }

    public List<Integer> setPlaylist(int[] idArray) {
        Stack<Integer> missingIdsList;
        missingIdsList = new Stack<Integer>();
        playlist = idArray;

        if ( idArray != null ) {
            int arrayLen = idArray.length;

            for (int index = 0; index < arrayLen; index++) {
                int id = idArray[index];
                AudioStreamItem audioItem = findAudioItemById(id);
                if ( audioItem==null || !audioItem.isPoopulated() ) {
                    // We need the audio information for this ID
                    Log.d(TAG, "setPlaylist: Need info for: " + id);
                    missingIdsList.push(id);
                }
            }

        }
        return missingIdsList;
    }

    private AudioStreamItem findAudioItemById(int id) {
        AudioStreamItem audioItem = null;
        if ( id != 0) {
            audioItem = audioList.get(id);
        }
        return audioItem;
    }

    public void parseReadList(String readListXmlString) {
        
        TrackList trackList;
        Serializer serializer = new Persister();

        try {
            trackList = serializer.read( TrackList.class, readListXmlString);

            if ( trackList != null ) {
                for (TrackListEntry entry : trackList.entries) {
                    addInfoToAudioList( entry);
                }
            } else {
                Log.e(TAG, "parseReadList: Tracklist is null" );
            }
        } catch (Exception e) {
            Log.e(TAG, "parseReadList: Could not parse the ReadList XML: " + e.toString(),e );
        }
    }

    private void addInfoToAudioList(TrackListEntry entry) {
        if (entry != null) {
            AudioStreamItem audioItem = parseDIDL(entry.metadata);
            if ( audioItem != null) {
                int id = Integer.parseInt(entry.id);

                audioItem.setId(id);
                audioItem.setAudioUrl(entry.uri);
                Log.d(TAG, "addInfoToAudioList: Added audio item: " + audioItem.toString());
                audioList.put(id, audioItem);
            }
        }
    }
}
