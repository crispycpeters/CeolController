package com.candkpeters.ceol.cling;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.candkpeters.ceol.device.ImageDownloaderResult;
import com.candkpeters.ceol.device.ImageDownloaderTask;
import com.candkpeters.ceol.model.DeviceStatusType;
import com.candkpeters.ceol.model.PlayStatusType;
import com.candkpeters.ceol.model.SIStatusType;
import com.candkpeters.ceol.model.StreamingStatus;
import com.candkpeters.ceol.model.control.AudioControl;
import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.control.OpenhomePlaylistControl;
import com.candkpeters.ceol.model.control.ProgressControl;
import com.candkpeters.ceol.model.control.TrackControl;
import com.candkpeters.ceol.model.TrackList;
import com.candkpeters.ceol.model.TrackListEntry;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.android.AndroidUpnpService;
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
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;
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

class OpenHomeSubscriptionManager implements ImageDownloaderResult {
    private static final String TAG = "OpenHomeSubManager";

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
    private ServiceId infoServiceId = new ServiceId("av-openhome-org","Info");
    private ServiceId timeServiceId = new ServiceId("av-openhome-org","Time");
    private ServiceId playlistServiceId = new ServiceId("av-openhome-org","Playlist");
    private ServiceId volumeServiceId = new ServiceId("av-openhome-org","Volume");
    private Service timeService;
    private Service infoService;
    private Service playlistService;
    private Service volumeService;
    private ImageDownloaderTask imageDownloaderTask;
    private long totalTrackCount;


    OpenHomeSubscriptionManager(Context context, CeolModel ceolModel, OnSubscriptionListener onSubscriptionListener) {
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
        timeService = infoService = playlistService = null;
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
        infoService = findService(infoServiceId);
        playlistService = findService(playlistServiceId);
        timeService = findService(timeServiceId);
        volumeService = findService(volumeServiceId);

        setupVolumeEvents();
        setupTimeEvents();
        setupInfoEvents();
        setupPlaylistEvents();
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

    private Service findService( ServiceId serviceId) {
        Service service;
        if ((service = device.findService(serviceId)) == null) {
            Log.e(TAG, "No service for " + serviceId);
        }
        return service;
    }

    void performPlaylistCommand(String command) {
        if ( playlistService != null ) {
            executeAction( playlistService, command);
        }
    }

    void performPlaylistSeekIdCommand(int trackId) {
        if ( playlistService != null ) {
            executeActionSeekId( trackId);
        }
    }

    void performPlaylistSeekSecondAbsoluteCommand(int absoluteSeconds) {
        if ( playlistService != null ) {
            executeActionSeekSecondAbsolute( absoluteSeconds);
        }
    }

    private void setupVolumeEvents() {
        if (volumeService != null) {

//            executeAction(upnpService, infoService);

            SubscriptionCallback callback = new OpenHomeSubscriptionCallback(volumeService) {

                @Override
                public void eventReceived(GENASubscription sub) {

                    Log.d(TAG,"Volume Event: " + sub.getCurrentSequence().getValue());
                    try {
                        if ( isOperating()) {
                            Map<String, StateVariableValue> values = sub.getCurrentValues();

                            UnsignedIntegerFourBytes volumeV = (UnsignedIntegerFourBytes) (values.get("Volume").getValue());
                            Log.d(TAG, "EVENT: GOT volume=" + volumeV);
                            audioControl.updateMasterVolumePerCent(volumeV.getValue());

                            ceolModel.notifyObservers(audioControl);
                        }
                    } catch ( Exception e ) {
                        Log.e( TAG, "Bad values from event: " + e);
                    }
                }

            };
            upnpService.getControlPoint().execute(callback);
        }
    }

    private void setupTimeEvents() {
        if (timeService != null) {

//            executeAction(upnpService, infoService);

            SubscriptionCallback callback = new OpenHomeSubscriptionCallback(timeService) {

                @Override
                public void eventReceived(GENASubscription sub) {

                    long eventSequence = sub.getCurrentSequence().getValue();

                    try {
                        Map<String, StateVariableValue> values = sub.getCurrentValues();

                        UnsignedIntegerFourBytes durationV = (UnsignedIntegerFourBytes)(values.get("Duration").getValue());
//                        Log.d(TAG, "EVENT: GOT duration=" + durationV);

                        long duration = durationV.getValue();
                        if ( trackControl.getAudioItem().getDuration() != duration) {
                            trackControl.getAudioItem().setDuration((int)duration);
                            ceolModel.notifyObservers(trackControl);
                        }

                        UnsignedIntegerFourBytes secondsV = (UnsignedIntegerFourBytes)(values.get("Seconds").getValue());

                        if ( eventSequence % 10 == 0 ) {
                            Log.d(TAG, "Getting time ev (every sec): GOT seconds=" + secondsV + " sequence=" + eventSequence);
                        }
                        progressControl.updateProgress(secondsV.getValue());

                        notifyInputControlIsOpenhome();
                        ceolModel.notifyObservers(progressControl);

                    } catch ( Exception e ) {
                        Log.e( TAG, "Bad values from event: " + e);
                    }
                }

            };
            upnpService.getControlPoint().execute(callback);
        }
    }

    private void setupInfoEvents() {
        if (infoService != null) {
            final ImageDownloaderResult imageDownloaderResult = this;
//            executeAction(upnpService, infoService);

            SubscriptionCallback callback = new OpenHomeSubscriptionCallback(infoService) {

                @Override
                public void eventReceived(GENASubscription sub) {

                    isSubscribed = true;
                    Log.d(TAG, "Info Event: " + sub.getCurrentSequence().getValue());

                    Map<String, StateVariableValue> values = sub.getCurrentValues();

                    UnsignedIntegerFourBytes trackCountV = (UnsignedIntegerFourBytes) (values.get("TrackCount").getValue());
                    Log.d(TAG, "EVENT: GOT trackCount=" + trackCountV);
                    long trackCount = trackCountV.getValue();
                    setTotalTrackCount(trackCount);

                    notifyInputControlIsOpenhome();

                    if (isOperating()) {
                        StateVariableValue metadata = values.get("Metadata");
                        Log.d(TAG, "EVENT: GOT metadata=" + metadata);
//                    ceolDeviceOpenHome.setMetadata((String)(metadata.getValue()));
                        AudioStreamItem audioStreamItem = parseDIDL((String) (metadata.getValue()));

                        StateVariableValue uri = values.get("Uri");
                        Log.d(TAG, "EVENT: GOT uri=" + uri);
                        audioStreamItem.setAudioUrl((String) (uri.getValue()));

//                    trackControl.getAudioStreamItem().setAudioUrl((String)(uri.getValue()));

//                    URI oldImageUri = trackControl.getAudioStreamItem().getImageBitmapUrl();

                        if (!audioStreamItem.equals(trackControl.getAudioItem())) {
                            Log.d(TAG, "eventReceived: New stream is different: " + audioStreamItem);
                            trackControl.updateAudioItem(audioStreamItem);
                            imageDownloaderTask = new ImageDownloaderTask(imageDownloaderResult);
                            if (audioStreamItem.getImageBitmapUrl() != null) {
                                imageDownloaderTask.execute(audioStreamItem);
                            }
                        }
                        ceolModel.notifyObservers(trackControl);
                    }
                }


                @Override
                public void ended(GENASubscription sub,
                                  CancelReason reason,
                                  UpnpResponse response) {
                    Log.d(TAG, "Info subscription ended: " + reason);

                    if (reason == null) {
                        Log.d(TAG, "ended: Service must have shutdown.");
                        removeDevice();
                        if (onSubscriptionListener != null) {
                            onSubscriptionListener.onDeviceDisconnected();
                        }
                    } else {
                        switch (reason) {

                            case RENEWAL_FAILED:
                            case EXPIRED:
                                // TODO - Need to inform manager and go into a retry...
                                isSubscribed = false;
                                if (onSubscriptionListener != null) {
                                    onSubscriptionListener.onSubscriptionDisconnected();
                                }
                                break;
                            case UNSUBSCRIBE_FAILED:
                                break;
                            case DEVICE_WAS_REMOVED:
                                removeDevice();
                                if (onSubscriptionListener != null) {
                                    onSubscriptionListener.onDeviceDisconnected();
                                }
                                break;
                        }
                    }

                }


            };
            upnpService.getControlPoint().execute(callback);
        }
    }

    @Override
    public void imageDownloaded(AudioStreamItem item) {
        Log.d(TAG, "imageDownloaded: Downloaded!");
        trackControl.getAudioItem().setImageBitmap(item.getImageBitmap());
        ceolModel.notifyObservers(trackControl);
    }

    private void notifyInputControlIsOpenhome() {
        if ( !ceolModel.connectionControl.isConnected()) {
            ceolModel.notifyConnectionStatus(true);
        }
        if ( ceolModel.powerControl.updateDeviceStatus(DeviceStatusType.On) ) {
            ceolModel.notifyObservers(ceolModel.powerControl);
        }
        if ( isOperating() && ceolModel.inputControl.getStreamingStatus() != StreamingStatus.OPENHOME) {
            ceolModel.inputControl.updateSIStatus(SIStatusType.OpenHome);
            ceolModel.notifyObservers(ceolModel.inputControl);
        }
    }

    private void setupPlaylistEvents() {
        if (playlistService != null) {
            SubscriptionCallback callback = new OpenHomeSubscriptionCallback(playlistService) {

                @Override
                public void eventReceived(GENASubscription sub) {

                    Log.d(TAG,"Playlist Event: " + sub.getCurrentSequence().getValue());

                    Map<String, StateVariableValue> values = sub.getCurrentValues();

                    StateVariableValue transportState = values.get("TransportState");
                    Log.d(TAG, "EVENT: GOT transportState=" + transportState);
                    setTransportState(transportState.getValue().toString());
//                    ceolDeviceOpenHome.setTransportState((String)(transportState.getValue()));

                    StateVariableValue idArrayVal = values.get("IdArray");
                    setupIds( idArrayVal );

                    UnsignedIntegerFourBytes currentTrackIdValue = (UnsignedIntegerFourBytes) (values.get("Id").getValue());
                    Log.d(TAG, "EVENT: GOT Id=" + currentTrackIdValue);
                    setCurrentTrackId(currentTrackIdValue.getValue());

                    ceolModel.notifyObservers(ceolModel.inputControl.trackControl);
                    ceolModel.notifyObservers(ceolModel.inputControl.playlistControl);

                    notifyInputControlIsOpenhome();
                }

            };
            upnpService.getControlPoint().execute(callback);
        }
    }

    private void setCurrentTrackId(long currentTrackId) {
        OpenhomePlaylistControl openhomePlaylistControl = (OpenhomePlaylistControl)ceolModel.inputControl.playlistControl;
        openhomePlaylistControl.setCurrentTrackId(currentTrackId);
    }

    private void setTransportState(String value) {
        // Not sure this is needed
        PlayStatusType playStatusType = PlayStatusType.Unknown;
        switch (value) {
            case "Playing":
                playStatusType = PlayStatusType.Playing;
                break;
            case "Paused":
                playStatusType = PlayStatusType.Paused;
                break;
            case "Buffering":
                break;
            case "Stopped":
                playStatusType = PlayStatusType.Stopped;
                break;
        }
        ceolModel.inputControl.trackControl.updatePlayStatus(playStatusType);
    }

    private void setupIds(StateVariableValue idArrayVal) {
        OpenhomePlaylistControl openhomePlaylistControl = (OpenhomePlaylistControl)ceolModel.inputControl.playlistControl;
//        OpenhomePlaylistControl openhomePlaylistControl = new OpenhomePlaylistControl();

        byte byteBuf[] = (byte [])idArrayVal.getValue();
        int[] array = null;

        if ( byteBuf != null) {
            IntBuffer intBuf = ByteBuffer.wrap(byteBuf).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
            array = new int[intBuf.remaining()];
            intBuf.get(array);
        }

        requestMissingAudioData( openhomePlaylistControl.setPlaylist( array) );
    }

    private void requestMissingAudioData(List<Integer> missingIds) {
        if ( missingIds.size() > 0 ) {
            StringBuilder buf = new StringBuilder();

            for (int id : missingIds) {
                buf.append( id);
                buf.append(" ");
            }
            executeActionReadList(buf.toString());
        }
    }


    private void executeAction(final Service service, final String action) {
        ActionInvocation actionInvocation = new ActionInvocation(service.getAction(action));

        upnpService.getControlPoint().execute(new ActionCallback(actionInvocation) {

                                                  @Override
                                                  public void success(ActionInvocation invocation) {
                                                      //assert invocation.getOutput().length == 0;
                                                      Log.d(TAG,"Successfully called action: " + action);
                                                  }

                                                  @Override
                                                  public void failure(ActionInvocation invocation,
                                                                      UpnpResponse operation,
                                                                      String defaultMsg) {
                                                      System.err.println(defaultMsg);
                                                  }
                                              }
        );
    }

    private void executeActionReadList(String missingIdString) {
        ActionInvocation actionInvocation = new ActionInvocation(playlistService.getAction("ReadList"));
        actionInvocation.setInput("IdList",missingIdString);

        upnpService.getControlPoint().execute(new ActionCallback(actionInvocation) {

                                                  @Override
                                                  public void success(ActionInvocation invocation) {
                                                      Log.d(TAG,"Successfully called action: Readlist");

                                                      parseReadList(invocation);
                                                      ceolModel.notifyObservers(ceolModel.inputControl.playlistControl);

                                                  }

                                                  @Override
                                                  public void failure(ActionInvocation invocation,
                                                                      UpnpResponse operation,
                                                                      String defaultMsg) {
                                                      System.err.println(defaultMsg);
                                                  }
                                              }
        );
    }

    private void executeActionSeekId(int trackId) {
        final String action = "SeekId";
        ActionInvocation actionInvocation = new ActionInvocation(playlistService.getAction(action));
        UnsignedIntegerFourBytes value = new UnsignedIntegerFourBytes(trackId);
        actionInvocation.setInput("Value",value);

        upnpService.getControlPoint().execute(new ActionCallback(actionInvocation) {

                                                  @Override
                                                  public void success(ActionInvocation invocation) {
                                                      Log.d(TAG,"Successfully called action: " + action);

//                                                      ceolModel.notifyObservers(ceolModel.inputControl.playlistControl);
                                                  }

                                                  @Override
                                                  public void failure(ActionInvocation invocation,
                                                                      UpnpResponse operation,
                                                                      String defaultMsg) {
                                                      System.err.println(defaultMsg);
                                                  }
                                              }
        );
    }

    private void executeActionSeekSecondAbsolute(int absoluteSeconds) {
        final String action = "SeekSecondAbsolute";
        ActionInvocation actionInvocation = new ActionInvocation(playlistService.getAction(action));
        UnsignedIntegerFourBytes value = new UnsignedIntegerFourBytes(absoluteSeconds);
        actionInvocation.setInput("Value",value);

        upnpService.getControlPoint().execute(new ActionCallback(actionInvocation) {

                                                  @Override
                                                  public void success(ActionInvocation invocation) {
                                                      Log.d(TAG,"Successfully called action: " + action);

//                                                      ceolModel.notifyObservers(ceolModel.inputControl.playlistControl);
                                                  }

                                                  @Override
                                                  public void failure(ActionInvocation invocation,
                                                                      UpnpResponse operation,
                                                                      String defaultMsg) {
                                                      System.err.println(defaultMsg);
                                                  }
                                              }
        );
    }

    private void parseReadList(ActionInvocation invocation) {
        ActionArgumentValue values[] = invocation.getOutput();

        if (values.length == 1) {
            parseReadList((String)(values[0].getValue()));
        }
        else {
            Log.e(TAG, "parseReadList: Received no ReadList!");
        }
    }

    private void parseReadList(String readListXmlString) {

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
//                Log.d(TAG, "addInfoToAudioList: Added audio item: " + audioItem.toString());
                openhomePlaylistControl.putItem(id, audioItem);
            }
        }
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

    abstract class OpenHomeSubscriptionCallback extends SubscriptionCallback {

        OpenHomeSubscriptionCallback(Service service) {
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
