package com.candkpeters.ceol.device.wss;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.candkpeters.ceol.device.ImageDownloaderResult;
import com.candkpeters.ceol.device.ImageDownloaderTask;
import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.DeviceStatusType;
import com.candkpeters.ceol.model.PlayStatusType;
import com.candkpeters.ceol.model.control.CeolNavigatorControl;
import com.candkpeters.ceol.model.control.OpenhomePlaylistControl;
import com.candkpeters.ceol.model.control.PlaylistControlBase;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;


public class WssClient implements ImageDownloaderResult {

    private static final String TAG = "WssClient";

    private static final int CONNECTION_RETRY_MSECS = 60000;
    private static final String UPDATE_REQUEST = "{\"type\":\"UPDATE\",\"body\":\"\"}";
    private static final long BACKOFF_TIMEOUT_MSECS = 30000;
    private static final long BACKOFF_RECONNECT_WAITTIME = 300000;
    private final CeolModel ceolModel;
    private WebSocket webSocket;
    private WebSocketFactory webSocketFactory;
    private ImageDownloaderTask imageDownloaderTask;
    private static final long IMAGE_LOAD_DELAY_MSECS = 10;
    private static final String IMAGEURLSPEC = "/image";
    private URL imageUrl;
    private long prevImageTimeStamp = -1;
    private boolean backOffTimeoutStarted;
    private long backOffStartTime;
    private boolean isStarting = false;

    WssClient(CeolModel ceolModel) {
        this.ceolModel = ceolModel;
        // Create a WebSocketFactory instance.
        webSocketFactory = new WebSocketFactory();
        webSocketFactory.setConnectionTimeout(2000);
    }

    private void connectToWebSocket( URI uri ) {
        final URI furi = uri;
        try {
            webSocket = webSocketFactory.createSocket(uri);
            Log.i("WebSocket", "Socket created");

            // Register a listener to receive WebSocket events.
            webSocket.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) {
                    Log.i(TAG, "Message received: " + message);
                    updateCeolModel(message);
                }

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
//                    super.onConnected(websocket, headers);

                    Log.i(TAG, "Connected. Session is starting");
                    ceolModel.notifyConnectionStatus(true);
                    websocket.sendText(UPDATE_REQUEST);
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
//                    super.onConnectError(websocket, exception);
                    Log.w(TAG, "Connection error. Retry after " + CONNECTION_RETRY_MSECS + " msecs");
                    webSocket = null;
                    ceolModel.notifyConnectionStatus(false);
                    retryConnectToWebSocket(furi);
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
//                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                    Log.i(TAG, "Disconnected");
                    webSocket = null;
                    ceolModel.notifyConnectionStatus(false);
                    retryConnectToWebSocket(furi);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Connect
        if ( webSocket != null) {
            try {
                // Connect to the server and perform an opening handshake.
                // This method blocks until the opening handshake is finished.
                webSocket.connectAsynchronously();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void retryConnectToWebSocket(URI uri) {
        if ( isStarting ) {

            final URI furi = uri;
            Handler handler = new Handler(Looper.getMainLooper());
            final Runnable r = new Runnable() {
                public void run() {
                    connectToWebSocket(furi);
                }
            };
            handler.postDelayed(r, CONNECTION_RETRY_MSECS);
        }
    }

    private void startWebSocketClient(String server) {
        if ( webSocket != null ) {
            try {
                webSocket.disconnect();
                Log.i(TAG, "Socket disconnect performed");
            } catch ( Exception e) {
                Log.i(TAG, "startWebSocketClient: Closing any existing connection");
            }
        } else {
            URI uri;
//        resetBackOffCounters();
            try {
                // Connect to local host
                uri = new URI(server);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return;
            }
            connectToWebSocket(uri);
        }
    }

    private StringBuilder fullmessage = null;

    private void updateCeolModel(String fulls) {
            if ( fulls==null || fulls.length() ==0 ) {
            return;
        }
        final char prefix = fulls.charAt(0);
        final String messageChunk = fulls.substring(1);
        boolean messageComplete = false;
        switch ( prefix ) {
            case '=':
                // Data is in one line
                fullmessage = new StringBuilder(messageChunk);
                messageComplete = true;
                break;
            case '<':
                // Start of data on multiple messages
                fullmessage = new StringBuilder(messageChunk);
                messageComplete = false;
                break;
            case '>':
                messageComplete = true;
                break;
            case '-':
                fullmessage.append(messageChunk);
                messageComplete = false;
                break;
        }
        if ( !messageComplete ) {
//            Log.i("webSocket", "We have partial message: " + messageChunk);
            return;
        }
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<CeolDataRoot> jsonAdapter = moshi.adapter(CeolDataRoot.class);
        try {
            CeolDataRoot ceolDataRoot = jsonAdapter.fromJson(fullmessage.toString());
            if (ceolDataRoot.ceolData != null) {
                CeolData ceolData = ceolDataRoot.ceolData;
                Log.i("webSocket", "Got CeolData volume: " + ceolData.volume);

                populateCeolData(ceolData);

            }
            if (ceolDataRoot.ohPlaylist != null) {
                OhPlaylist ohPlaylist = ceolDataRoot.ohPlaylist;
                Log.i("webSocket", "Got OhPlaylist Id: " + ohPlaylist.Id);

                populateOhPlaylist( ceolModel.inputControl.playlistControl, ohPlaylist);

            }
        }
        catch (Exception e){
            e.printStackTrace();

        }
        notifyObservers();
    }

    private void populateCeolData(CeolData ceolData) {
        AudioStreamItem audioItem = new AudioStreamItem();

        ceolModel.notifyConnectionStatus(true);

        ceolModel.inputControl.updateSIStatus(ceolData.source);
        if (ceolModel.powerControl.getDeviceStatus() != DeviceStatusType.On && ceolData.power.equals("ON")) {
            // We're switching on CEOL - need to reload any images
            Log.i(TAG, "Power on - reload images");
            prevImageTimeStamp = -1;
        }
        ;
        ceolModel.powerControl.updateDeviceStatus(ceolData.power);
        ceolModel.audioControl.updateMasterVolume(ceolData.volume);

        switch (ceolData.source) {
            case "ANALOGIN":
            case "BLUETOOTH":
            case "DIGITALIN1":
            case "DIGITALIN2":
            case "CD":
                break;
            case "TUNER":
                audioItem.setBand(ceolData.tuner.band);
                audioItem.setFrequency(ceolData.tuner.frequency);
                audioItem.setTitle(ceolData.tuner.name);
                ceolModel.inputControl.updateSIStatus(ceolData.input);
                break;
            case "MUSIC SERVER":
            case "SPOTIFYCONNECT":
            case "INTERNET RADIO":
            case "OPENHOME":
                CeolNavigatorControl newCeolNavigatorControl = new CeolNavigatorControl();

                newCeolNavigatorControl.setIsBrowsing(ceolData.type.compareTo("browse") == 0);

                newCeolNavigatorControl.initialiseChunk(
                        ceolData.browse.title, ceolData.browse.scridValue,
                        ceolData.browse.scrid, ceolData.browse.listmax, ceolData.browse.listposition);
                for (int i = 0; i < ceolData.browse.items.length; i++) {
                    CeolData.CeolDataBrowseItem item = ceolData.browse.items[i];
                    if (item.title != null) {
                        newCeolNavigatorControl.setChunkLine(i, item.title, item.desc + (item.current ? "s" : ""));
                    }
                }
                ceolModel.inputControl.navigatorControl = newCeolNavigatorControl;

                audioItem.setTitle(ceolData.net.track);
                audioItem.setArtist(ceolData.net.artist);
                audioItem.setAlbum(ceolData.net.album);
                audioItem.setBitrate(ceolData.net.bitrate);
                audioItem.setFormat(ceolData.net.format);
                audioItem.setImageBitmapUrl(imageUrl);
                audioItem.setDuration(ceolData.net.duration);

                setPlayStatus(ceolData.net.playstatus);
                break;
        }
        ceolModel.inputControl.trackControl.updateAudioItem(audioItem);

        if ( prevImageTimeStamp == -1 || ceolData.imageTimeStamp != prevImageTimeStamp ) {
            prevImageTimeStamp = ceolData.imageTimeStamp;
            getImage(ceolModel.inputControl.trackControl.getAudioItem());
        }
    }

    private void populateOhPlaylist(PlaylistControlBase playlistControl, OhPlaylist ohPlaylist) {
        OpenhomePlaylistControl openhomePlaylistControl = (OpenhomePlaylistControl)playlistControl;

        try {
            openhomePlaylistControl.setCurrentTrackId(Integer.parseInt(ohPlaylist.Id));
            int arrlen = ohPlaylist.entries != null ? ohPlaylist.entries.length : 0;

            int[] idArray = new int[arrlen];

            for ( int i=0; i<arrlen; i++ ) {
                OhPlaylist.OhEntry entry = ohPlaylist.entries[i];
                int id = Integer.parseInt(entry.Id);
                idArray[i] = id;

                AudioStreamItem audioStreamItem = new AudioStreamItem();
                audioStreamItem.setTitle(entry.title);
                audioStreamItem.setAlbum(entry.album);
                audioStreamItem.setArtist(entry.artist);
                audioStreamItem.setAudioUrl(entry.Url);
                audioStreamItem.setBitrate(entry.bitrate);
                audioStreamItem.setId(id);
                if (entry.Url != null) audioStreamItem.setAudioUrl(entry.Url);
                if (entry.albumArtUri != null) audioStreamItem.setImageBitmapUrl(new URL(entry.albumArtUri));
                audioStreamItem.setDuration(parseDuration(entry.duration));
                openhomePlaylistControl.putItem(id,audioStreamItem);
            }
            openhomePlaylistControl.setPlaylist(idArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void start(String wssServer) {
        try {
            imageUrl = new URL(new URL("http://" + wssServer ), IMAGEURLSPEC);
        } catch ( MalformedURLException e) {
            Log.e(TAG, "Image URL problem: Bad URL: " + wssServer+ " + " + IMAGEURLSPEC,e );
        }
        isStarting = true;
        startWebSocketClient("ws://" + wssServer + "/");
    }

    public void stop() {
        Log.d(TAG, "In stop()");
        isStarting = false;
        if (webSocket != null) {
            webSocket.disconnect();
        }
    }

    public void sendCommand(String commandString) {
        String wssCommand = "{\"type\":\"CMD\",\"body\":\"" + commandString + "\"}";
        Log.i(TAG,"sendCommand + "+ wssCommand);
        if ( webSocket != null) {
            webSocket.sendText( wssCommand);
        }
    }

    private void setPlayStatus(String playStatusString) {
        if ( playStatusString != null) {
            switch (playStatusString.toUpperCase()) {
                case "PLAY":
                    ceolModel.inputControl.trackControl.updatePlayStatus(PlayStatusType.Playing);
                    return;
                case "PAUSE":
                    ceolModel.inputControl.trackControl.updatePlayStatus(PlayStatusType.Paused);
                    return;
                case "STOP":
                    ceolModel.inputControl.trackControl.updatePlayStatus(PlayStatusType.Stopped);
                    return;
                default:
                    ceolModel.inputControl.trackControl.updatePlayStatus(PlayStatusType.Unknown);
            }
        }
    }

    private void getImage(final AudioStreamItem audioItem) {

        if ( imageDownloaderTask == null || !imageDownloaderTask.isRunning()) {
            Log.d(TAG, "getImage: Initiating delayed download");
            imageDownloaderTask = new ImageDownloaderTask(this);
//            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        imageDownloaderTask.execute(audioItem);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, IMAGE_LOAD_DELAY_MSECS);
            imageDownloaderTask.execute(audioItem);
        }
    }

    @Override
    public void imageDownloaded(AudioStreamItem item) {
        updateDeviceImage(item);
    }

    private void updateDeviceImage(AudioStreamItem item) {
        AudioStreamItem audioStreamItem = ceolModel.inputControl.trackControl.getAudioItem();
        if (audioStreamItem == null) {
            Log.w(TAG, "updateDeviceImage: Cannot update bitmap as the audio item is not an AudioStreamItem. Have we changed SI input?" );
        } else {
            if ( item.equals(audioStreamItem)) {
                Log.d(TAG, "updateDeviceImage: Updating image in AudioStreamItem: " + audioStreamItem.toString());
                audioStreamItem.setImageBitmap(item.getImageBitmap());
//                trackControlChanged = true;
                notifyObservers();
            }
        }
    }

    private void notifyObservers() {
        ceolModel.notifyObservers(null);
    }

}
