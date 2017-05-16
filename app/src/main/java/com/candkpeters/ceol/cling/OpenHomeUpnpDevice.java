package com.candkpeters.ceol.cling;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.candkpeters.ceol.device.ImageDownloaderResult;
import com.candkpeters.ceol.device.ImageDownloaderTask;
import com.candkpeters.ceol.model.control.AudioControl;
import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.control.OpenhomePlaylistControl;
import com.candkpeters.ceol.model.control.TrackControl;
import com.candkpeters.ceol.model.TrackList;
import com.candkpeters.ceol.model.TrackListEntry;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;

/**
 * Created by crisp on 14/04/2017.
 */

public class OpenHomeUpnpDevice implements ImageDownloaderResult {
    private static final String TAG = "OpenHomeUpnpDevice";

    private static final int DEFAULT_EVENT_RENEWAL_SECS = 600;
    private final Context context;
    private final CeolModel ceolModel;
    private final OpenhomePlaylistControl openhomePlaylistControl;
    private final TrackControl trackControl;
    private final AudioControl audioControl;
    private Device device;
    private AndroidUpnpService upnpService;
    private ServiceId infoServiceId = new ServiceId("av-openhome-org","Info");
    private ServiceId timeServiceId = new ServiceId("av-openhome-org","Time");
    private ServiceId playlistServiceId = new ServiceId("av-openhome-org","Playlist");
    private ServiceId volumeServiceId = new ServiceId("av-openhome-org","Volume");
    private Service timeService;
    private Service infoService;
    private Service playlistService;
    private Service volumeService;
    private ImageDownloaderTask imageDownloaderTask;
    private DIDLContent didlContent;



    public OpenHomeUpnpDevice(Context context, CeolModel ceolModel) {
        this.context = context;
        this.ceolModel = ceolModel;
        //TODO - Should be using an interface or base methods in PlaylistControlBase
        this.openhomePlaylistControl = (OpenhomePlaylistControl)ceolModel.inputControl.playlistControl;
        this.audioControl = ceolModel.audioControl;
        this.trackControl = ceolModel.inputControl.trackControl;
//        ceolDeviceOpenHome = ceolModel.getOpenHome();
    }

    public void removeDevice() {
        device = null;
        timeService = infoService = playlistService = null;
//        ceolDeviceOpenHome.setDuration(0);
//        ceolDeviceOpenHome.setTotalTrackCount(0);
//        ceolDeviceOpenHome.setSeconds(0);
//        ceolDeviceOpenHome.setAudioUri("");
//        ceolDeviceOpenHome.setMetadata("");
    }

    public Device getDevice() {
        return device;
    }

    public void addDevice(AndroidUpnpService upnpService, Device device) {
        this.upnpService = upnpService;
        this.device = device;
        DeviceDetails dd = device.getDetails();
        Log.d(TAG, "Details: " + dd.toString());
        DeviceIdentity di = device.getIdentity();
        Log.d(TAG, "Identity: " + di.toString());

        infoService = findService(infoServiceId);
        playlistService = findService(playlistServiceId);
        timeService = findService(timeServiceId);
        volumeService = findService(volumeServiceId);

        setupVolumeEvents();
        setupTimeEvents();
        setupInfoEvents();
        setupPlaylistEvents();
    }

    private Service findService( ServiceId serviceId) {
        Service service;
        if ((service = device.findService(serviceId)) != null) {
            Log.d(TAG, "Service discovered: " + service);
        } else {
            Log.e(TAG, "No service for " + serviceId);
        }
        return service;
    }

    public void performPlaylistCommand( String command) {
        if ( playlistService != null ) {
            executeAction( playlistService, command);
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
                        Map<String, StateVariableValue> values = sub.getCurrentValues();

                        UnsignedIntegerFourBytes volumeV = (UnsignedIntegerFourBytes)(values.get("Volume").getValue());
                        Log.d(TAG, "EVENT: GOT volume=" + volumeV);
                        audioControl.updateMasterVolumePerCent((long) (volumeV.getValue()));

                        ceolModel.notifyObservers(audioControl);
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

                    Log.d(TAG,"Time Event: " + sub.getCurrentSequence().getValue());

                    try {
                        Map<String, StateVariableValue> values = sub.getCurrentValues();

                        UnsignedIntegerFourBytes durationV = (UnsignedIntegerFourBytes)(values.get("Duration").getValue());
                        Log.d(TAG, "EVENT: GOT duration=" + durationV);
//                        ceolDeviceOpenHome.setDuration((long) (durationV.getValue()));

                        UnsignedIntegerFourBytes secondsV = (UnsignedIntegerFourBytes)(values.get("Seconds").getValue());
                        Log.d(TAG, "EVENT: GOT seconds=" + secondsV);
                        trackControl.updateProgress((long) (secondsV.getValue()));

                        ceolModel.notifyObservers(trackControl);

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

                    Log.d(TAG,"Info Event: " + sub.getCurrentSequence().getValue());

                    Map<String, StateVariableValue> values = sub.getCurrentValues();

                    UnsignedIntegerFourBytes trackCountV = (UnsignedIntegerFourBytes)(values.get("TrackCount").getValue());
                    Log.d(TAG, "EVENT: GOT trackCount=" + trackCountV);
//                    ceolDeviceOpenHome.setTotalTrackCount((long)(trackCountV.getValue()));

                    StateVariableValue metadata = values.get("Metadata");
                    Log.d(TAG, "EVENT: GOT metadata="+metadata);
//                    ceolDeviceOpenHome.setMetadata((String)(metadata.getValue()));
                    AudioStreamItem audioStreamItem = parseDIDL((String)(metadata.getValue()));

                    StateVariableValue uri = values.get("Uri");
                    Log.d(TAG, "EVENT: GOT uri="+uri);
                    audioStreamItem.setAudioUrl((String)(uri.getValue()));

//                    trackControl.getAudioStreamItem().setAudioUrl((String)(uri.getValue()));

//                    URI oldImageUri = trackControl.getAudioStreamItem().getImageBitmapUri();

                    if ( !audioStreamItem.equals(trackControl.getAudioItem())  ) {
                        Log.d(TAG, "eventReceived: New stream is different: "+audioStreamItem);
                        trackControl.updateAudioItem(audioStreamItem);
                        imageDownloaderTask = new ImageDownloaderTask(imageDownloaderResult);
                        if ( audioStreamItem.getImageBitmapUri() != null ) {
                            imageDownloaderTask.execute(audioStreamItem.getImageBitmapUri().toString());
                        }
                    }
                    ceolModel.notifyObservers(trackControl);
                }

            };
            upnpService.getControlPoint().execute(callback);
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
//                    ceolDeviceOpenHome.setTransportState((String)(transportState.getValue()));

                    StateVariableValue idArrayVal = values.get("IdArray");


                    setupIds( idArrayVal );

                    ceolModel.notifyObservers(ceolModel.inputControl.playlistControl);
//                    ceolDevice.notifyObservers();
/*

                    StateVariableValue uri = values.get("Uri");
                    Log.d(TAG, "EVENT: GOT uri="+uri);
                    ceolDeviceOpenHome.setAudioUri((String)(uri.getValue()));

                    StateVariableValue metadata = values.get("Metadata");
                    Log.d(TAG, "EVENT: GOT metadata="+metadata);
                    ceolDeviceOpenHome.setMetadata((String)(metadata.getValue()));
*/
                }

            };
            upnpService.getControlPoint().execute(callback);
        }
    }

    private void setupIds(StateVariableValue idArrayVal) {
        OpenhomePlaylistControl openhomePlaylistControl = (OpenhomePlaylistControl)ceolModel.inputControl.playlistControl;

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
            StringBuffer buf = new StringBuffer();

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
                Log.d(TAG, "addInfoToAudioList: Added audio item: " + audioItem.toString());
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
                audioItem.setDuration(parseDuration(res.getDuration()));
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

    private int parseDuration(String durationString) {
        //String source = "00:10:17.x";
        try {
            String[] tokens = durationString.split(":");
            int secondsToSeconds = Integer.parseInt(tokens[2]) ;
            int minutesToSeconds = Integer.parseInt(tokens[1]) * 60;
            int hoursToSeconds = Integer.parseInt(tokens[0]) * 3600;
            return secondsToSeconds + minutesToSeconds + hoursToSeconds;
        } catch (Exception e) {
            Log.e(TAG, "setDuration: Formatting error for: "+durationString, e );
            return 0;
        }
    }


    @Override
    public void imageDownloaded(Bitmap bitmap) {
        Log.d(TAG, "imageDownloaded: Downloaded!");
        trackControl.getAudioItem().setImageBitmap(bitmap);
        ceolModel.notifyObservers(trackControl);
    }

    public abstract class OpenHomeSubscriptionCallback extends SubscriptionCallback {

        protected OpenHomeSubscriptionCallback(Service service) {
            super(service, DEFAULT_EVENT_RENEWAL_SECS);
        }

        @Override
        public void established(GENASubscription sub) {
            Log.d(TAG, "Established: " + sub.getSubscriptionId());
        }

        @Override
        protected void failed(GENASubscription subscription,
                              UpnpResponse responseStatus,
                              Exception exception,
                              String defaultMsg) {
            Log.d(TAG,"Subscripiton failed: " + defaultMsg);
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

    };

}
