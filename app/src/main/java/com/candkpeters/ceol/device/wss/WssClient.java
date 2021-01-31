package com.candkpeters.ceol.device.wss;

import android.util.Log;

import com.candkpeters.ceol.device.ImageDownloaderResult;
import com.candkpeters.ceol.device.ImageDownloaderTask;
import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.DeviceStatusType;
import com.candkpeters.ceol.model.PlayStatusType;
import com.candkpeters.ceol.model.control.CeolNavigatorControl;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import tech.gusavila92.websocketclient.WebSocketClient;

public class WssClient implements ImageDownloaderResult {

    private static final String TAG = "WssClient";

    private static final String UPDATE_REQUEST = "{\"type\":\"UPDATE\",\"body\":\"\"}";
    private static final long BACKOFF_TIMEOUT_MSECS = 30000;
    private static final long BACKOFF_RECONNECT_WAITTIME = 300000;
    private final CeolModel ceolModel;
    private WebSocketClient webSocketClient;
    private ImageDownloaderTask imageDownloaderTask;
    private static final long IMAGE_LOAD_DELAY_MSECS = 10;
    private static final String IMAGEURLSPEC = "/image";
    private URL imageUrl;
    private long prevImageTimeStamp = -1;
    private boolean backOffTimeoutStarted;
    private long backOffStartTime;

    WssClient(CeolModel ceolModel) {
        this.ceolModel = ceolModel;
    }

    private void startWebSocketClient(String server) {
        if ( webSocketClient != null ) {
            try {
                webSocketClient.close();
            } catch ( Exception e) {
                Log.i(TAG, "startWebSocketClient: Closing any existing connection");
            }
        }
        URI uri;
//        resetBackOffCounters();
        try {
            // Connect to local host
            uri = new URI( server);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Session is starting");
//                resetBackOffCounters();
                ceolModel.notifyConnectionStatus(true);
                nudge();
//                webSocketClient.send("Hello World!");
            }

            @Override
            public void onTextReceived(String s) {
                Log.i("WebSocket", "Message received: " + s);

                updateCeolModel(s);
            }
            @Override
            public void onBinaryReceived(byte[] data) {
            }
            @Override
            public void onPingReceived(byte[] data) {
            }
            @Override
            public void onPongReceived(byte[] data) {
            }
            @Override
            public void onException(Exception e) {
                Log.w("onException", e.getMessage());
                if ( e instanceof ConnectException) {
                    ceolModel.notifyConnectionStatus(false);
//                    if ( shouldBackOff() ) {
//                        webSocketClient.enableAutomaticReconnection(BACKOFF_RECONNECT_WAITTIME);
//                    };
                }
            }
            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
                System.out.println("onCloseReceived");
                ceolModel.notifyConnectionStatus(false);
                webSocketClient = null;
            }
        };
        webSocketClient.setConnectTimeout(3000);
        webSocketClient.setReadTimeout(0);
        webSocketClient.enableAutomaticReconnection(BACKOFF_RECONNECT_WAITTIME);
        webSocketClient.connect();
    }

    private void updateCeolModel(String s) {
        final String message = s;
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<CeolData> jsonAdapter = moshi.adapter(CeolData.class);
        try {
            CeolData ceolData = jsonAdapter.fromJson(message);
            Log.i("webSocket", "Got volume: " + ceolData.volume);

            AudioStreamItem audioItem = new AudioStreamItem();

            ceolModel.notifyConnectionStatus(true);

            ceolModel.inputControl.updateSIStatus(ceolData.source);
            if ( ceolModel.powerControl.getDeviceStatus() == DeviceStatusType.Starting && ceolData.power.equals("ON") ) {
                // We're switching on CEOL - need to reload any images
                Log.i( TAG, "Power on - reload images");
                prevImageTimeStamp = -1;
            };
            ceolModel.powerControl.updateDeviceStatus(ceolData.power);
            ceolModel.audioControl.updateMasterVolume(ceolData.volume);

            switch ( ceolData.source) {
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

                    CeolNavigatorControl newCeolNavigatorControl = new CeolNavigatorControl();

                    newCeolNavigatorControl.setIsBrowsing(ceolData.type.compareTo("browse")==0);

                    newCeolNavigatorControl.initialiseChunk(
                            ceolData.browse.title, ceolData.browse.scridValue,
                            ceolData.browse.scrid, ceolData.browse.listmax, ceolData.browse.listposition);
                    for (int i = 0; i < ceolData.browse.items.length; i++) {
                        CeolData.CeolDataBrowseItem item = ceolData.browse.items[i];
                        if ( item.title != null ) {
                            newCeolNavigatorControl.setChunkLine(i, item.title, item.desc + (item.current?"s":""));
                        }
                    }
                    ceolModel.inputControl.navigatorControl = newCeolNavigatorControl;

                    audioItem.setTitle(ceolData.net.track);
                    audioItem.setArtist(ceolData.net.artist);
                    audioItem.setAlbum(ceolData.net.album);
                    audioItem.setBitrate(ceolData.net.bitrate);
                    audioItem.setFormat(ceolData.net.format);
                    audioItem.setImageBitmapUrl(imageUrl);

                    setPlayStatus(ceolData.net.playstatus);
                    break;
            }
            ceolModel.inputControl.trackControl.updateAudioItem(audioItem);

            if ( prevImageTimeStamp == -1 || ceolData.imageTimeStamp != prevImageTimeStamp ) {
                prevImageTimeStamp = ceolData.imageTimeStamp;
                getImage(ceolModel.inputControl.trackControl.getAudioItem());
            }

        }
        catch (Exception e){
            e.printStackTrace();

        }
        notifyObservers();
        //                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try{
//                            TextView textView = findViewById(R.id.debugOutTextView);
//                            textView.setText(message);
//
//                            Moshi moshi = new Moshi.Builder().build();
//                            JsonAdapter<CeolData> jsonAdapter = moshi.adapter(CeolData.class);
//                            CeolData ceolData = jsonAdapter.fromJson(message);
//                            Log.i("webSocket", "Got: " + ceolData);
//                        } catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                });
    }

//    private void resetBackOffCounters() {
//        backOffTimeoutStarted = false;
//        backOffStartTime = 0L;
//    }

//    private boolean shouldBackOff() {
//        if (backOffTimeoutStarted) {
//            if ( System.currentTimeMillis() - backOffStartTime > BACKOFF_TIMEOUT_MSECS ) {
//                return true;
//            }
//        } else {
//            backOffStartTime = System.currentTimeMillis();
//            backOffTimeoutStarted = true;
//        }
//        return false;
//    }

    public void start(String wssServer) {
        try {
            imageUrl = new URL(new URL("http://" + wssServer ), IMAGEURLSPEC);
        } catch ( MalformedURLException e) {
            Log.e(TAG, "Image URL problem: Bad URL: " + wssServer+ " + " + IMAGEURLSPEC,e );
        }
        startWebSocketClient("ws://" + wssServer + "/");
    }

    public void stop() {
//        if (webSocketClient != null) {
//            webSocketClient.close();
//        }
    }

    public void sendCommand(String commandString) {
        String wssCommand = "{\"type\":\"CMD\",\"body\":\"" + commandString + "\"}";
        Log.i("sendCommand", wssCommand);
        if ( webSocketClient != null) {
            webSocketClient.send( wssCommand);
        }
    }

    public void nudge() {
        if ( webSocketClient != null ) {
            webSocketClient.send(UPDATE_REQUEST);
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
