package com.candkpeters.ceol.device;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.control.CeolNavigatorControl;
import com.candkpeters.ceol.model.PlayStatusType;
import com.candkpeters.ceol.model.SIStatusType;
import com.candkpeters.ceol.model.StreamingStatus;
import com.candkpeters.ceol.view.Prefs;
import com.candkpeters.ceol.view.UIThreadUpdater;

import org.simpleframework.xml.util.Dictionary;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

/**
 * Created by crisp on 08/01/2016.
 */
class CeolWebSvcGatherer extends GathererBase implements Runnable, ImageDownloaderResult /*, Observed */{

    private static final String TAG = "CeolWebSvcGatherer";

    private static final int REPEATRATE_MSECS = 900;
    private static final int BACKGROUNDRATE_MSECS = 1800000;
    private static final int REPEATONCE_MSECS = 500;
    private static final long IMAGE_LOAD_DELAY_MSECS = 1000;

    private WebSvcApiService webSvcApiService = null;
    private UIThreadUpdater activeThreadUpdater;
    private final CeolModel ceolModel;
    private URL imageUrl;
    private final Object MUTEX = new Object();

    private final static String statusQueryString_NetServer = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<tx>\n" +
            " <cmd id=\"1\">GetPowerStatus</cmd>\n" +
            " <cmd id=\"2\">GetVolumeLevel</cmd>\n" +
            " <cmd id=\"3\">GetNetAudioStatus</cmd>\n" +
            " <cmd id=\"4\">GetSourceStatus</cmd>\n" +
            "</tx>\n";
    private TypedString statusQuery_NetServer = new TypedString(statusQueryString_NetServer);
    private final static String statusQueryString_CD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<tx>\n" +
            " <cmd id=\"1\">GetPowerStatus</cmd>\n" +
            " <cmd id=\"2\">GetVolumeLevel</cmd>\n" +
            " <cmd id=\"3\">GetCDStatus</cmd>\n" +
            " <cmd id=\"4\">GetSourceStatus</cmd>\n" +
            "</tx>\n";
    private TypedString statusQuery_CD = new TypedString(statusQueryString_CD);
    private final static String statusQueryString_Tuner = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<tx>\n" +
            " <cmd id=\"1\">GetPowerStatus</cmd>\n" +
            " <cmd id=\"2\">GetVolumeLevel</cmd>\n" +
            " <cmd id=\"3\">GetTunerStatus</cmd>\n" +
            " <cmd id=\"4\">GetSourceStatus</cmd>\n" +
            "</tx>\n";
    private TypedString statusQuery_Tuner = new TypedString(statusQueryString_Tuner);
    private ImageDownloaderTask imageDownloaderTask;
    private static final String IMAGEURLSPEC = "/NetAudio/art.asp-jpg";
    private boolean powerControlChanged = false;
    private boolean inputControlChanged = false;
    private boolean trackControlChanged = false;
    private boolean audioControlChanged = false;
    private boolean ceolNavigatorControlChanged = false;
    private boolean isActive = false;

    CeolWebSvcGatherer(Context context, CeolModel ceolModel) {
        this.ceolModel = ceolModel;

        initiatlizeControls();
    }

    @Override
    public void run() {
        initiatlizeControls();
        if ( !ceolModel.connectionControl.isConnected() || ceolModel.inputControl.getStreamingStatus() != StreamingStatus.OPENHOME ) {
//        if (!ceolDevice.isOpenHomeOperating() || isTimeForBackground()) {
            getStatus_Async();
//        } else {
//            activeThreadUpdater.next();
//        }
        }
    }

    private void initiatlizeControls() {
        powerControlChanged = false;
        inputControlChanged = false;
        trackControlChanged = false;
        audioControlChanged = false;
        ceolNavigatorControlChanged = false;
    }

    private void notifyObservers() {
        ceolModel.notifyObservers(null);
/*
        if (powerControlChanged) {
            Log.d(TAG, "notifyObservers: powerControl has changed");
            ceolModel.notifyObservers(ceolModel.powerControl);
        }
        if (audioControlChanged) {
            Log.d(TAG, "notifyObservers: audioControl has changed");
            ceolModel.notifyObservers(ceolModel.audioControl);
        }
        if (inputControlChanged) {
            Log.d(TAG, "notifyObservers: inputControl has changed");
            ceolModel.notifyObservers(ceolModel.inputControl);
        }
        if (trackControlChanged) {
            Log.d(TAG, "notifyObservers: trackControl has changed");
            ceolModel.notifyObservers(ceolModel.inputControl.trackControl);
        }
        if (ceolNavigatorControlChanged) {
            Log.d(TAG, "notifyObservers: ceolNavigatorControl has changed");
            ceolModel.notifyObservers(ceolModel.inputControl.navigatorControl);
        }
*/
    }

    private void checkPowerControlChanged(boolean powerControlChanged) {
        if ( powerControlChanged) {
            Log.d(TAG, "checkPowerControlChanged: true");
            this.powerControlChanged = true;
        }
    }

    private void checkInputControlChanged(boolean inputControlChanged) {
        if ( inputControlChanged) {
            Log.d(TAG, "checkInputControlChanged: true");
            this.inputControlChanged = true;
        }
    }

    private void checkTrackControlChanged(boolean trackControlChanged) {
        if ( trackControlChanged) {
            Log.d(TAG, "checkTrackControlChanged: true");
            this.trackControlChanged = true ;
        }
    }

    private void checkAudioControlChanged(boolean audioControlChanged) {
        if ( audioControlChanged) {
            Log.d(TAG, "checkAudioControlChanged: true");
            this.audioControlChanged = true;
        }
    }

    private void recreateService(String baseUrl) {
        webSvcApiService = WebSvcGenerator.createService(baseUrl);
        try {
            imageUrl = new URL(new URL(baseUrl), IMAGEURLSPEC);
        } catch ( MalformedURLException e) {
            Log.e(TAG, "recreateService: Bad URL: " + baseUrl+ " + " + IMAGEURLSPEC,e );
        }
    }

    void getStatusSoon() {
        activeThreadUpdater.fireOnce(REPEATONCE_MSECS);
//        resetBackgroundCountdown();
    }

    private void startActiveUpdates() {
        if ( activeThreadUpdater == null ) {
            activeThreadUpdater = new UIThreadUpdater(this, REPEATRATE_MSECS);
        }
        activeThreadUpdater.startUpdates();
    }

    private void initiateDelayedUpdate() {
        if ( activeThreadUpdater == null ) {
            activeThreadUpdater = new UIThreadUpdater(this, REPEATRATE_MSECS);
        }
        activeThreadUpdater.next();
    }

    private void stopActiveUpdates() {
        isActive = false;
        if (activeThreadUpdater != null) {
            activeThreadUpdater.stopUpdates();
        }
    }

    @Override
    public void start(Prefs prefs) {
        Log.d(TAG, "start: Starting gatherer");
        isActive = true;
        recreateService(prefs.getBaseUrl());
        startActiveUpdates();
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop: Stopping gatherer");
        isActive = false;
        stopActiveUpdates();
    }

    private void logdTime( String tag, String msg) {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Log.d(tag, currentDateTimeString + ": " + msg);
    }

    private void getStatus_Async() {
        webSvcApiService.appStatusLiteAsync(new Callback<WebSvcHttpStatusLiteResponse>() {
            @Override
            public void success(WebSvcHttpStatusLiteResponse webSvcHttpStatusLiteResponse, Response response) {
                //Log.d(TAG, "success: Got successful response: " + response.getBody());
                //Log.d(TAG, "success: power: " + webSvcHttpAppCommandResponse.power);
//                logdTime(TAG, "CEOL StatusLite success: ");
                if ( isActive) {
                    ceolModel.notifyConnectionStatus(true);
                    updateDeviceStatusLite(webSvcHttpStatusLiteResponse);
                    getStatus2_Async();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                logdTime(TAG, "CEOL StatusLite failed: " + error);
                updateDeviceErrorStatus();
                if ( isActive) {
                    initiateDelayedUpdate();
                }
            }
        });
    }

    private void getStatus2_Async() {
        TypedString statusQuery = determineStatusQuery(ceolModel.inputControl.getSIStatus());
        webSvcApiService.appCommandAsync(statusQuery, new Callback<WebSvcHttpAppCommandResponse>() {
            @Override
            public void success(WebSvcHttpAppCommandResponse webSvcHttpAppCommandResponse, Response response) {
                //Log.d(TAG, "success: Got successful response: " + response.getBody());
                //Log.d(TAG, "success: power: " + webSvcHttpAppCommandResponse.power);
//                logdTime(TAG, "CEOL AppCommand success: ");
                if ( isActive) {
                    ceolModel.notifyConnectionStatus(true);
                    updateDeviceStatus(webSvcHttpAppCommandResponse);
                    initiateDelayedUpdate();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                logdTime(TAG, "CEOL AppCommand error: " + error);
                updateDeviceErrorStatus();
                if ( isActive) {
                    initiateDelayedUpdate();
                }
            }
        });
    }


    private void getImage(final AudioStreamItem audioItem) {

        if ( imageDownloaderTask == null || !imageDownloaderTask.isRunning()) {
            Log.d(TAG, "getImage: Initiating delayed download");
            imageDownloaderTask = new ImageDownloaderTask(this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        imageDownloaderTask.execute(audioItem);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, IMAGE_LOAD_DELAY_MSECS);
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
                trackControlChanged = true;
                notifyObservers();
            }
        }
    }


    private void logResponse(WebSvcHttpAppCommandResponse webSvcHttpAppCommandResponse) {
        Log.d(TAG, "logResponse: " + webSvcHttpAppCommandResponse.toString());
    }

    private TypedString determineStatusQuery(SIStatusType siStatus) {

        switch (siStatus) {
            case CD:
                return statusQuery_CD;
            case Tuner:
                return statusQuery_Tuner;
            //TODO case AnalogIn:
            //case IRadio:
            default:
                return statusQuery_NetServer;
        }
    }


    private void updateDeviceErrorStatus() {
//        synchronized (ceolDevice) {
//            ceolDevice.setDeviceStatus(DeviceStatusType.Connecting);
//            ceolDevice.updateSIStatus(SIStatusType.Unknown);
//            ceolDevice.updateSIStatus(SIStatusType.Unknown);
//        }
//        ceolDevice.notifyObservers();
//        onCeolStatusChangedListener.onCeolStatusChanged(ceolDevice);
        ceolModel.notifyConnectionStatus(false);
    }

    private void updateDeviceStatusLite(WebSvcHttpStatusLiteResponse webSvcHttpStatusLiteResponse) {
        checkPowerControlChanged(ceolModel.powerControl.updateDeviceStatus(webSvcHttpStatusLiteResponse.power));
        checkInputControlChanged(ceolModel.inputControl.updateSIStatus(webSvcHttpStatusLiteResponse.inputFunc));
//        ceolDevi  ce.setIsMuted(webSvcHttpStatusLiteResponse.mute.equals("on"));
        notifyObservers();
    }

    private void setPlayStatus(String playStatusString) {
        if ( playStatusString != null) {
            switch (playStatusString.toUpperCase()) {
                case "PLAY":
                    checkTrackControlChanged(ceolModel.inputControl.trackControl.updatePlayStatus(PlayStatusType.Playing));
                    return;
                case "PAUSE":
                    checkTrackControlChanged(ceolModel.inputControl.trackControl.updatePlayStatus(PlayStatusType.Paused));
                    return;
                case "STOP":
                    checkTrackControlChanged(ceolModel.inputControl.trackControl.updatePlayStatus(PlayStatusType.Stopped));
                    return;
                default:
                    checkTrackControlChanged(ceolModel.inputControl.trackControl.updatePlayStatus(PlayStatusType.Unknown));
            }
        }
    }

    private void updateDeviceStatus(WebSvcHttpAppCommandResponse webSvcHttpAppCommandResponse) {

        AudioStreamItem oldAudioItem = ceolModel.inputControl.trackControl.getAudioItem();

        try {

            checkInputControlChanged(ceolModel.inputControl.updateSIStatus(webSvcHttpAppCommandResponse.source));
            checkPowerControlChanged(ceolModel.powerControl.updateDeviceStatus(webSvcHttpAppCommandResponse.power));
            checkAudioControlChanged(ceolModel.audioControl.updateMasterVolume(webSvcHttpAppCommandResponse.dispvalue));

            StreamingStatus streamingStatus = ceolModel.inputControl.getStreamingStatus();
            if ( streamingStatus == StreamingStatus.SPOTIFY || streamingStatus == StreamingStatus.CEOL  ) {
                CeolNavigatorControl newCeolNavigatorControl = new CeolNavigatorControl();

                setPlayStatus( webSvcHttpAppCommandResponse.playstatus);

                Dictionary<WebSvcHttpResponseText> texts = webSvcHttpAppCommandResponse.texts;
                if (texts != null) {

                    newCeolNavigatorControl.setIsBrowsing(true);
                    if (webSvcHttpAppCommandResponse.type.equals("browse")) {
                        newCeolNavigatorControl.initialiseChunk(texts.get("title").text,
                                texts.get("scridValue").text,
                                texts.get("scrid").text,
                                webSvcHttpAppCommandResponse.listmax,
                                webSvcHttpAppCommandResponse.listposition);
                        for (int i = 0; i < CeolNavigatorControl.MAX_LINES; i++) {
                            WebSvcHttpResponseText responseText = texts.get("line" + i);
                            if (responseText != null) {
                                newCeolNavigatorControl.setChunkLine(i, responseText.text, responseText.flag);
                            }
                        }
                    }

                    AudioStreamItem audioItem = new AudioStreamItem();
                    audioItem.setAudioItem(ceolModel.inputControl.trackControl.getAudioItem());
//                    if ( ceolModel.inputControl.trackControl.getPlayStatus() == PlayStatusType.Stopped) {
//                        audioItem.setStreamInfo(0,"","","","", "");
//                        audioItem.setImageBitmap(null);
//                    } else {
                        if (webSvcHttpAppCommandResponse.type.equals("play")) {
                            audioItem.setStreamInfo(
                                    0,
                                    texts.get("track").text,
                                    texts.get("artist").text,
                                    texts.get("album").text,
                                    texts.get("format").text,
                                    texts.get("bitrate").text
                            );
                            newCeolNavigatorControl.setIsBrowsing(false);
                            audioItem.setImageBitmapUrl(imageUrl);
                        }
//                    }
                    checkTrackControlChanged(ceolModel.inputControl.trackControl.updateAudioItem(audioItem));
                } else {
                    Log.d(TAG, "updateDeviceStatus: null text");
                    newCeolNavigatorControl.clear();
                }
                if ( !newCeolNavigatorControl.equals(ceolModel.inputControl.navigatorControl)) {
                    ceolModel.inputControl.navigatorControl = newCeolNavigatorControl;
                    ceolNavigatorControlChanged = true;
                }

            } else {
                AudioStreamItem audioItem = new AudioStreamItem();
                checkTrackControlChanged(ceolModel.inputControl.trackControl.updatePlayStatus(PlayStatusType.Playing));
                switch (ceolModel.inputControl.getSIStatus()) {

                    case Unknown:
                        break;
                    case CD:
                        // TODO
                        break;
                    case Tuner:
                        audioItem.setBand(webSvcHttpAppCommandResponse.band);
                        audioItem.setFrequency(webSvcHttpAppCommandResponse.frequency);
                        audioItem.setTitle(webSvcHttpAppCommandResponse.name);
                        audioItem.setAuto(webSvcHttpAppCommandResponse.automanual != null && webSvcHttpAppCommandResponse.automanual.equalsIgnoreCase("AUTO"));
                        break;
                    default:
                        break;
                }
                checkTrackControlChanged(ceolModel.inputControl.trackControl.updateAudioItem(audioItem));
            }

        } catch (Exception e) {
            Log.w(TAG, "updateDeviceStatus: Exception in web response: " + e.toString());
            e.printStackTrace();
        }


/*
        if ( ceolModel.inputControl.trackControl.getAudioItem().getImageBitmapUrl() != null ) {
            AudioStreamItem audioStreamItem = ceolModel.inputControl.trackControl.getAudioItem()
            Picasso.with(context)
                    .load(String.valueOf(audioStreamItem.getImageBitmapUrl()))
                    .stableKey(audioStreamItem.getKey())
                    .into(imageV);

        }
*/

        if ( !oldAudioItem.equals(ceolModel.inputControl.trackControl.getAudioItem())) {
            getImage(ceolModel.inputControl.trackControl.getAudioItem());
        }

/*
        if ( imageDownloaderTask == null ||
                (!imageDownloaderTask.isRunning() &&
                        (oldTrack == null || !oldTrack.equals(ceolModel.inputControl.trackControl.getAudioItem().getTitle()) ))) {
            getImage();
        }
*/



//        ceolModel.setAudioControl(audioControlUpdate);
//        ceolModel.setInputControl(inputControl);
//        ceolModel.setTrackControl(trackControl);
//        ceolModel.setPowerControl(powerControl);
//        onCeolStatusChangedListener.onCeolStatusChanged(ceolDevice);
        notifyObservers();
//        ceolModel.updateControlsAndNotify(powerControlUpdate, inputControlUpdate, audioControlUpdate, trackControlUpdate, ceolNavigatorControlUpdate);
//        if ( ceol.getSIStatus() != oldSiStatus) {
//        }
    }



}
