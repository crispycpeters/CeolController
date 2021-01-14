package com.candkpeters.ceol.device.wss;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.candkpeters.ceol.device.ImageDownloaderResult;
import com.candkpeters.ceol.device.ImageDownloaderTask;
import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.PlayStatusType;
import com.candkpeters.ceol.model.control.CeolNavigatorControl;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import tech.gusavila92.websocketclient.WebSocketClient;

public class WssClient implements ImageDownloaderResult {

    private static final String TAG = "WssClient";

    private static final String UPDATE_REQUEST = "{\"type\":\"UPDATE\",\"body\":\"\"}";
    private final CeolModel ceolModel;
    private WebSocketClient webSocketClient;
    private ImageDownloaderTask imageDownloaderTask;
    private static final long IMAGE_LOAD_DELAY_MSECS = 10;
    private static final String IMAGEURLSPEC = "/image";
    private URL imageUrl;
    private long prevImageTimeStamp = -1;

    WssClient(CeolModel ceolModel) {
        this.ceolModel = ceolModel;
    }

    private void startWebSocketClient(String server) {
        URI uri;
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
                send(UPDATE_REQUEST);
//                webSocketClient.send("Hello World!");
            }
            @Override
            public void onTextReceived(String s) {
                Log.i("WebSocket", "Message received: " + s);

                final String message = s;
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<CeolData> jsonAdapter = moshi.adapter(CeolData.class);
                try {
                    CeolData ceolData = jsonAdapter.fromJson(message);
                    Log.i("webSocket", "Got volume: " + ceolData.volume);

                    AudioStreamItem audioItem = new AudioStreamItem();

                    ceolModel.notifyConnectionStatus(true);

                    ceolModel.inputControl.updateSIStatus(ceolData.source);
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
                        case "SPOTIFY":
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
                System.out.println(e.getMessage());
            }
            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
                System.out.println("onCloseReceived");
                ceolModel.notifyConnectionStatus(false);
                webSocketClient = null;
            }
        };
        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }

    public void start(String wssServer) {
        try {
            imageUrl = new URL(new URL("http://" + wssServer ), IMAGEURLSPEC);
        } catch ( MalformedURLException e) {
            Log.e(TAG, "Image URL problem: Bad URL: " + wssServer+ " + " + IMAGEURLSPEC,e );
        }
        if ( webSocketClient == null ) {
            startWebSocketClient("ws://" + wssServer + "/");
        }
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
