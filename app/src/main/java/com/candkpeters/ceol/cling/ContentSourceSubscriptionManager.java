package com.candkpeters.ceol.cling;

import android.content.Context;
import android.util.Log;

import com.candkpeters.ceol.device.ImageDownloaderResult;
import com.candkpeters.ceol.device.ImageDownloaderTask;
import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.DeviceStatusType;
import com.candkpeters.ceol.model.PlayStatusType;
import com.candkpeters.ceol.model.SIStatusType;
import com.candkpeters.ceol.model.StreamingStatus;
import com.candkpeters.ceol.model.TrackList;
import com.candkpeters.ceol.model.TrackListEntry;
import com.candkpeters.ceol.model.control.AudioControl;
import com.candkpeters.ceol.model.control.OpenhomePlaylistControl;
import com.candkpeters.ceol.model.control.ProgressControl;
import com.candkpeters.ceol.model.control.TrackControl;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.item.Item;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;

/**
 * Created by crisp on 14/04/2017.
 */

class ContentSourceSubscriptionManager extends SubscriptionManager implements ImageDownloaderResult {
    private static final String TAG = "ContentSrcSubMgr";

    private static final int DEFAULT_EVENT_RENEWAL_SECS = 100;
    private final CeolModel ceolModel;
    private final OpenhomePlaylistControl openhomePlaylistControl;
    private final TrackControl trackControl;
    private final AudioControl audioControl;
    private final ProgressControl progressControl;
    private final OnSubscriptionListener onSubscriptionListener;
    private boolean isSubscribed;
    private Device device;
    private UpnpService upnpService;
    private Service contentDirectoryService;
    private ImageDownloaderTask imageDownloaderTask;
    private long totalTrackCount;

    ContentSourceSubscriptionManager(Context context, CeolModel ceolModel, OnSubscriptionListener onSubscriptionListener) {
        this.ceolModel = ceolModel;
        //TODO - Should be using an interface or base methods in PlaylistControlBase
        this.openhomePlaylistControl = (OpenhomePlaylistControl)ceolModel.inputControl.playlistControl;
        this.audioControl = ceolModel.audioControl;
        this.trackControl = ceolModel.inputControl.trackControl;
        this.progressControl = ceolModel.progressControl;
        this.isSubscribed = false;
        this.onSubscriptionListener = onSubscriptionListener;

//        addDevice();
    }

    void removeDevice() {
        device = null;
        isSubscribed = false;
        contentDirectoryService = null;
        totalTrackCount = 0;
        this.openhomePlaylistControl.clearTracklist();
    }

    public Device getDevice() {
        return device;
    }

    boolean hasDevice() {
        return device != null;
    }

    void subscribe() {
        contentDirectoryService = findService(device, new UDAServiceId("ContentDirectory"));
    }

    void addDevice(UpnpService upnpService, Device device) {
        this.device = device;
        this.upnpService = upnpService;

//        DeviceDetails dd = device.getDetails();
//        Log.d(TAG, "Details: " + dd.toString());
//        DeviceIdentity di = device.getIdentity();
//        Log.d(TAG, "Identity: " + di.toString());
        subscribe();
    }

    @Override
    public void imageDownloaded(AudioStreamItem item) {
        Log.d(TAG, "imageDownloaded: Downloaded!");
        trackControl.getAudioItem().setImageBitmap(item.getImageBitmap());
        ceolModel.notifyObservers(trackControl);
    }

    public void performBrowseCommand(String directoryID, final String parent) {
        if ( contentDirectoryService != null ) {
            executeActionBrowse( directoryID, parent);
        }
    }

    private void executeActionBrowse(String directoryID,
                                    final String parent)
    {
        upnpService.getControlPoint().execute(new Browse(contentDirectoryService, directoryID, BrowseFlag.DIRECT_CHILDREN, "*", 0,
                null, new SortCriterion(true, "dc:title")) {

            @Override
            public void received(ActionInvocation actionInvocation, final DIDLContent didl)
            {
                Log.v(TAG, "received!: " + didl.toString());
            }

            @Override
            public void updateStatus(Status status)
            {
                Log.v(TAG, "updateStatus ! ");
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg)
            {
                Log.w(TAG, "Fail to browse ! " + defaultMsg);
            }

        });
    }


    private AudioStreamItem parseDIDL(String metadata) {
        AudioStreamItem audioItem = new AudioStreamItem();
        if ( metadata != null && metadata.length() > 0 ) {
            System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
            DIDLParser didlParser = new DIDLParser();

            try {
                audioItem.clear();
//                Log.d(TAG, "parseDIDL: Parsing: "+ metadata);
                DIDLContent didlContent = didlParser.parse(metadata);
                if ( didlContent.getItems().size() > 1 )
                    throw new Exception("Should only have one item");

                Item item = didlContent.getItems().get(0);
                audioItem.setTitle(item.getTitle() );
                audioItem.setArtist(item.getFirstPropertyValue(DIDLObject.Property.UPNP.ARTIST.class).getName() );
                audioItem.setAlbum(item.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM.class) );
                Res res = item.getFirstResource();
                audioItem.setFormat(res.getProtocolInfo().getContentFormat());
                audioItem.setDuration(parseDuration(res.getDuration()));
                audioItem.setBitrate(Long.toString(res.getBitrate()/1000) + "k"); // TODO Wrong - should be using protocol info flags some way
                DIDLObject.Property<URI> didlUri = item.getFirstProperty(DIDLObject.Property.UPNP.ALBUM_ART_URI.class);
                if ( didlUri != null) {
                    audioItem.setImageBitmapUrl(new URL(didlUri.getValue().toString()));
                }
            } catch (Exception e) {
                Log.e(TAG, "parseDIDL: Bad XML in DIDL.", e);
            }
        }
        return audioItem;
    }

    private int parseDuration(String durationString) {
        //String source = "00:10:17.x";
        try {
            String[] tokens = durationString.split(":");

            float seconds = Float.parseFloat(tokens[2]);
            int secondsToSeconds = Math.round(seconds);
            int minutesToSeconds = Integer.parseInt(tokens[1]) * 60;
            int hoursToSeconds = Integer.parseInt(tokens[0]) * 3600;
            return secondsToSeconds + minutesToSeconds + hoursToSeconds;
        } catch (Exception e) {
            Log.e(TAG, "setDuration: Formatting error for: "+durationString, e );
            return 0;
        }
    }

    boolean isSubscribed() {
        return isSubscribed;
    }

    private boolean isOperating() {
        return isSubscribed && totalTrackCount > 0;
    }

    private void setTotalTrackCount(long totalTrackCount) {
        this.totalTrackCount = totalTrackCount;
    }

    @Override
    public void offerDevice(Device device) {

    }

    abstract class ContentSourceSubscriptionCallback extends SubscriptionCallback {

        ContentSourceSubscriptionCallback(Service service) {
            super(service, DEFAULT_EVENT_RENEWAL_SECS);
        }

        @Override
        public void established(GENASubscription sub) {
//            Log.d(TAG, "Established: " + sub.getSubscriptionId());
        }

        @Override
        protected void failed(GENASubscription subscription,
                              UpnpResponse responseStatus,
                              Exception exception,
                              String defaultMsg) {
            Log.d(TAG,"Subscription failed: " + defaultMsg);
            isSubscribed = false;
            removeDevice();
        }

        @Override
        public void ended(GENASubscription sub,
                          CancelReason reason,
                          UpnpResponse response) {
//                    assertNull(reason);
            Log.d(TAG,"Subscription ended: "+ reason);
        }

        @Override
        public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
            Log.d(TAG,"Missed events: " + numberOfMissedEvents);
        }

    }

}
