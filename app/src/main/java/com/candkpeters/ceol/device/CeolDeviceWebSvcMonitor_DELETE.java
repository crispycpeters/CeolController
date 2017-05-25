package com.candkpeters.ceol.device;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolDeviceNetServer_DELETE;
import com.candkpeters.ceol.model.CeolDevice_DELETE;
import com.candkpeters.ceol.model.DeviceStatusType;
import com.candkpeters.ceol.model.PlayStatusType;
import com.candkpeters.ceol.view.UIThreadUpdater;
import com.candkpeters.ceol.model.SIStatusType;
import com.candkpeters.ceol.model.CeolDeviceTuner_DELETE;

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
public class CeolDeviceWebSvcMonitor_DELETE implements Runnable, ImageDownloaderResult/*, Observed */{

    private static final String TAG = "CeolDeviceWebSvcMonitor_DELETE";

    private static final int REPEATRATE_MSECS = 9000;
    private static final int BACKGROUNDRATE_MSECS = 1800000;
    private static final int REPEATONCE_MSECS = 600;
//    private static final long BACKGROUNDTIMEOUT_MSECS = 10000;
//    private final int backgroundTimeoutMsecs;
//    private final int backgroundRateMsecs;

    public WebSvcApiService webSvcApiService = null;
    private UIThreadUpdater activeThreadUpdater;
    private UIThreadUpdater backgroundThreadUpdater;
    private int repeatrate;
    final public CeolDevice_DELETE ceolDevice;
    private URL imageUrl;

    // Observer
/*
    private final Object MUTEX = new Object();
    private List<OnCeolStatusChangedListener> observers;
*/

//    OnCeolStatusChangedListener onCeolStatusChangedListener;

    private final static String statusQueryString_NetServer = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<tx>\n" +
            " <cmd id=\"1\">GetPowerStatus</cmd>\n" +
            " <cmd id=\"2\">GetVolumeLevel</cmd>\n" +
            " <cmd id=\"3\">GetNetAudioStatus</cmd>\n" +
            " <cmd id=\"4\">GetSourceStatus</cmd>\n" +
            "</tx>\n";
    TypedString statusQuery_NetServer = new TypedString(statusQueryString_NetServer);
    private final static String statusQueryString_CD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<tx>\n" +
            " <cmd id=\"1\">GetPowerStatus</cmd>\n" +
            " <cmd id=\"2\">GetVolumeLevel</cmd>\n" +
            " <cmd id=\"3\">GetCDStatus</cmd>\n" +
            " <cmd id=\"4\">GetSourceStatus</cmd>\n" +
            "</tx>\n";
    TypedString statusQuery_CD = new TypedString(statusQueryString_CD);
    private final static String statusQueryString_Tuner = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<tx>\n" +
            " <cmd id=\"1\">GetPowerStatus</cmd>\n" +
            " <cmd id=\"2\">GetVolumeLevel</cmd>\n" +
            " <cmd id=\"3\">GetTunerStatus</cmd>\n" +
            " <cmd id=\"4\">GetSourceStatus</cmd>\n" +
            "</tx>\n";
    TypedString statusQuery_Tuner = new TypedString(statusQueryString_Tuner);
//    ImageDownloaderTask_DELETE imageDownloaderTask;
    ImageDownloaderTask imageDownloaderTask;
    private static final String IMAGEURLSPEC = "/NetAudio/art.asp-jpg";
    private long lastSuccessMsecs;

    public CeolDeviceWebSvcMonitor_DELETE(CeolDevice_DELETE ceolDevice, String baseUrl) {
        this.ceolDevice = ceolDevice;
/*
        this.observers=new ArrayList<OnCeolStatusChangedListener>();
*/
//        imageDownloaderTask = new ImageDownloaderTask_DELETE(this);
//        imageDownloaderTask = new ImageDownloaderTask(this);
        recreateService(baseUrl);
        try {
            imageUrl = new URL(new URL(baseUrl), IMAGEURLSPEC);
        } catch ( MalformedURLException e) {
            Log.e(TAG, "CeolDeviceWebSvcMonitor_DELETE: Bad URL: " + baseUrl+ " + " + IMAGEURLSPEC,e );
        }
//        resetBackgroundCountdown();
//        this.backgroundTimeoutMsecs = backgroundTimeoutMsecs;
//        this.backgroundRateMsecs = backgroundRateMsecs;
    }

    public void recreateService(String baseUrl) {
        webSvcApiService = WebSvcGenerator.createService(baseUrl);
    }

    public void getStatusSoon() {
        activeThreadUpdater.fireOnce(REPEATONCE_MSECS);
//        resetBackgroundCountdown();
    }

    public void startActiveUpdates() {

        //
        // TODO
        // TEMPORARY SWITCH OF OF MONITOR TO TEST GATHERER
        //
//        if ( activeThreadUpdater == null ) {
//            activeThreadUpdater = new UIThreadUpdater(this, REPEATRATE_MSECS);
//        }
//        activeThreadUpdater.startUpdates();
    }

    private void initiateDelayedUpdate() {
        if ( activeThreadUpdater == null ) {
            activeThreadUpdater = new UIThreadUpdater(this, REPEATRATE_MSECS);
        }
        activeThreadUpdater.next();
    }

    public void stopActiveUpdates() {
        if (activeThreadUpdater != null) {
            activeThreadUpdater.stopUpdates();
        }
    }

    public void start() {
/*
        if ( backgroundThreadUpdater == null ) {
            backgroundThreadUpdater = new UIThreadUpdater(this, backgroundRateMsecs);
            backgroundThreadUpdater.startUpdates();
        }
        checkBackgroundUpdate();
*/
    }

    public void getStatus_Sync() {
        try {
            WebSvcHttpStatusLiteResponse webSvcHttpStatusLiteResponse = webSvcApiService.appStatusLite();
            updateDeviceStatusLite(webSvcHttpStatusLiteResponse);
            getStatus2();
        } catch (RetrofitError retrofitError) {
            Log.w(TAG, "Could not connect to CEOL: " + retrofitError.getMessage());
            updateDeviceErrorStatus();
//            checkBackgroundUpdate();
        }
    }

    private void logdTime( String tag, String msg) {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Log.d(tag, currentDateTimeString + ": " + msg);
    }

    public void getStatus_Async() {
        webSvcApiService.appStatusLiteAsync(new Callback<WebSvcHttpStatusLiteResponse>() {
            @Override
            public void success(WebSvcHttpStatusLiteResponse webSvcHttpStatusLiteResponse, Response response) {
                //Log.d(TAG, "success: Got successful response: " + response.getBody());
                //Log.d(TAG, "success: power: " + webSvcHttpAppCommandResponse.power);
                logdTime(TAG, "StatusLite success: ");
                updateDeviceStatusLite(webSvcHttpStatusLiteResponse);
                getStatus2_Async();
            }

            @Override
            public void failure(RetrofitError error) {
                logdTime(TAG, "CEOL StatusLite failed: " + error);
                updateDeviceErrorStatus();
                initiateDelayedUpdate();
            }
        });
    }

    public void getStatus2() {
        TypedString statusQuery = determineStatusQuery(ceolDevice.getSIStatus());
        try {
            WebSvcHttpAppCommandResponse webSvcHttpAppCommandResponse = webSvcApiService.appCommand(statusQuery);
            if (webSvcHttpAppCommandResponse.texts == null) {
//                    Log.d(TAG, "success: Hmm - texts is null");
            } else {
                //Log.d(TAG, "success: titleView: " + webSvcHttpAppCommandResponse.texts.get("titleView"));
                logdTime(TAG, "AppCommand success: ");
            }
            updateDeviceStatus(webSvcHttpAppCommandResponse);
//            checkBackgroundUpdate();
        } catch ( RetrofitError retrofitError) {
            logdTime(TAG, "CEOL AppCommand failed: " + retrofitError.getMessage());
            updateDeviceErrorStatus();
//            checkBackgroundUpdate();
        }
    }

    public void getStatus2_Async() {
        TypedString statusQuery = determineStatusQuery(ceolDevice.getSIStatus());
        webSvcApiService.appCommandAsync(statusQuery, new Callback<WebSvcHttpAppCommandResponse>() {
            @Override
            public void success(WebSvcHttpAppCommandResponse webSvcHttpAppCommandResponse, Response response) {
                //Log.d(TAG, "success: Got successful response: " + response.getBody());
                //Log.d(TAG, "success: power: " + webSvcHttpAppCommandResponse.power);
                logdTime(TAG, "CEOL AppCommand success: ");
                updateDeviceStatus(webSvcHttpAppCommandResponse);
                initiateDelayedUpdate();
            }

            @Override
            public void failure(RetrofitError error) {
                logdTime(TAG, "CEOL AppCommand error: " + error);
                updateDeviceErrorStatus();
                initiateDelayedUpdate();
            }
        });
    }

    public void resetBackgroundCountdown() {
        lastSuccessMsecs = System.currentTimeMillis();
    }

    private void checkBackgroundUpdate() {
//        long currentMsecs = System.currentTimeMillis();
//        if (currentMsecs > lastSuccessMsecs + backgroundTimeoutMsecs) {
            // Time to back-off on active attempts
//            stopActiveUpdates();
//        } else {
            // Ensure updates are running
            startActiveUpdates();
//        }
    }

    public void getImage() {
        imageDownloaderTask = new ImageDownloaderTask(this);
//        imageDownloaderTask = new ImageDownloaderTask_DELETE(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
//                    imageDownloaderTask.execute();
                    imageDownloaderTask.execute(imageUrl.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
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


    public void updateDeviceImage(Bitmap bitmap) {
        synchronized (ceolDevice) {
            ceolDevice.getAudioItem().setImageBitmap(bitmap);
        }
    }

    private void updateDeviceErrorStatus() {
        synchronized (ceolDevice) {
            ceolDevice.setDeviceStatus(DeviceStatusType.Connecting);
            ceolDevice.setSIStatus(SIStatusType.Unknown);
//            ceolDevice.updateSIStatus(SIStatusType.Unknown);
        }
        ceolDevice.notifyObservers();
//        onCeolStatusChangedListener.onCeolStatusChanged(ceolDevice);
    }

    private void updateDeviceStatusLite(WebSvcHttpStatusLiteResponse webSvcHttpStatusLiteResponse) {
        ceolDevice.setDeviceStatus(webSvcHttpStatusLiteResponse.power);
        ceolDevice.setSIStatusLite(webSvcHttpStatusLiteResponse.inputFunc);
        ceolDevice.setIsMuted(webSvcHttpStatusLiteResponse.mute.equals("on"));
    }

    private void updateDeviceStatus(WebSvcHttpAppCommandResponse webSvcHttpAppCommandResponse) {
        SIStatusType oldSiStatus = ceolDevice.getSIStatus();

        String oldTrack = ceolDevice.getAudioItem().getTitle();
        if (oldTrack == null) {
            Log.d(TAG, "updateDeviceStatus: oldtrack is null");
        }

        try {

            synchronized (ceolDevice) {
                ceolDevice.setSIStatus(webSvcHttpAppCommandResponse.source);
                ceolDevice.setDeviceStatus(webSvcHttpAppCommandResponse.power);
                ceolDevice.setMasterVolume(webSvcHttpAppCommandResponse.dispvalue);

                if ( ceolDevice.isNetServer() ) {

                    CeolDeviceNetServer_DELETE ceolNetServer = ceolDevice.CeolNetServer;
                    AudioStreamItem audioItem = ceolDevice.getAudioItem();
                    ceolDevice.setPlayStatus( webSvcHttpAppCommandResponse.playstatus);

                    Dictionary<WebSvcHttpResponseText> texts = webSvcHttpAppCommandResponse.texts;
                    if (texts != null) {
                        ceolNetServer.setIsBrowsing(true);
                        if (webSvcHttpAppCommandResponse.type.equals("browse")) {
                            ceolNetServer.initialiseChunk(texts.get("titleView").text,
                                    texts.get("scridValue").text,
                                    texts.get("scrid").text,
                                    webSvcHttpAppCommandResponse.listmax,
                                    webSvcHttpAppCommandResponse.listposition);
                            for (int i = 0; i < CeolDeviceNetServer_DELETE.MAX_LINES; i++) {
                                WebSvcHttpResponseText responseText = texts.get("line" + i);
                                if (responseText != null) {
                                    ceolNetServer.setChunkLine(i, responseText.text, responseText.flag);
                                }
                            }
                        }
                        if ( ceolDevice.getPlayStatus() == PlayStatusType.Stopped) {
                            audioItem.setStreamInfo(0,"","","","", "");
                            audioItem.setImageBitmap(null);
                        } else {
                            if (webSvcHttpAppCommandResponse.type.equals("play")) {
                                audioItem.setStreamInfo(
                                        0,
                                        texts.get("track").text,
                                        texts.get("artist").text,
                                        texts.get("album").text,
                                        texts.get("format").text,
                                        texts.get("bitrate").text
                                );
                                ceolNetServer.setIsBrowsing(false);
                            }
                        }
                    } else {
                        ceolNetServer.clear();
                    }

                } else {
                    switch (ceolDevice.getSIStatus()) {

                        case Unknown:
                            break;
                        case CD:
                            // TODO
                            break;
                        case Tuner:
                            CeolDeviceTuner_DELETE tuner = ceolDevice.Tuner;
                            ceolDevice.setPlayStatus(PlayStatusType.Stopped);
                            tuner.setBand(webSvcHttpAppCommandResponse.band);
                            tuner.setFrequency(webSvcHttpAppCommandResponse.frequency);
                            tuner.setName(webSvcHttpAppCommandResponse.name);
                            tuner.setIsAuto(webSvcHttpAppCommandResponse.automanual == null ? false : webSvcHttpAppCommandResponse.automanual.equalsIgnoreCase("AUTO"));
                            break;
                        case AnalogIn:
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "updateDeviceStatus: Exception in web response: " + e.toString());
            e.printStackTrace();
        }

        if ( imageDownloaderTask == null ||
                (!imageDownloaderTask.isRunning() && (oldTrack == null || !ceolDevice.getAudioItem().getTitle().equalsIgnoreCase(oldTrack)))) {
            getImage();
        }

//        onCeolStatusChangedListener.onCeolStatusChanged(ceolDevice);
        ceolDevice.notifyObservers();
//        if ( ceol.getSIStatus() != oldSiStatus) {
//        }
    }

    @Override
    public void run() {
        if (!ceolDevice.isOpenHomeOperating() || isTimeForBackground()) {
            getStatus_Async();
        } else {
            activeThreadUpdater.next();
        }
    }

    private long lastBackgroundRun = 0;
    private boolean isTimeForBackground() {
        long now = System.currentTimeMillis();

        if ( now >= lastBackgroundRun + BACKGROUNDRATE_MSECS) {
            Log.d(TAG, "isTimeForBackground: Time for CEOL background status in OpenHome mode...");
            lastBackgroundRun = now;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void imageDownloaded(Bitmap bitmap) {
        updateDeviceImage(bitmap);
    }

/*
    @Override
    public int register(OnCeolStatusChangedListener obj) {
        int observerSize;
        synchronized (MUTEX) {
            if ( obj != null ) {
                if (!observers.contains(obj)) observers.add(obj);
            }
            observerSize = observers.size();
        }
        if ( observerSize == 1 ) {
            startActiveUpdates();
        }
        return observerSize;
    }

    @Override
    public int unregister(OnCeolStatusChangedListener obj) {
        int observerSize;
        synchronized (MUTEX) {
            if ( obj != null ) {
                observers.remove(obj);
            }
            observerSize = observers.size();
        }
        if ( observerSize  == 0 ) {
            stopActiveUpdates();
        }
        return observerSize;
    }

    @Override
    public void notifyObservers() {
        List<OnCeolStatusChangedListener> observersLocal = null;
        //synchronization is used to make sure any observer registered after message is received is not notified
        synchronized (MUTEX) {
            observersLocal = new ArrayList<>(this.observers);
        }
        for (OnCeolStatusChangedListener obj : observersLocal) {
            obj.onCeolStatusChanged(ceolDevice);
        }
    }
*/

}
