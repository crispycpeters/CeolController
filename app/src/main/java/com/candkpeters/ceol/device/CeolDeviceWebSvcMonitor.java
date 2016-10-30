package com.candkpeters.ceol.device;

import android.graphics.Bitmap;
import android.util.Log;

import com.candkpeters.ceol.model.DeviceStatusType;
import com.candkpeters.ceol.model.PlayStatusType;
import com.candkpeters.ceol.view.UIThreadUpdater;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.SIStatusType;
import com.candkpeters.ceol.model.CeolDeviceNetServer;
import com.candkpeters.ceol.model.CeolDeviceTuner;

import org.simpleframework.xml.util.Dictionary;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

/**
 * Created by crisp on 08/01/2016.
 */
public class CeolDeviceWebSvcMonitor implements Runnable, Observed{

    private static final String TAG = "CeolDeviceWebSvcMonitor";

    private static final int REPEATRATE_MSECS = 900;
//    private static final int BACKGROUNDRATE_MSECS = 5000;
    private static final int REPEATONCE_MSECS = 600;
//    private static final long BACKGROUNDTIMEOUT_MSECS = 10000;
    private final int backgroundTimeoutMsecs;
    private final int backgroundRateMsecs;

    public WebSvcApiService webSvcApiService = null;
    private UIThreadUpdater activeThreadUpdater;
    private UIThreadUpdater backgroundThreadUpdater;
    public CeolDevice ceolDevice;

    // Observer
    private final Object MUTEX = new Object();
    private List<OnCeolStatusChangedListener> observers;

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
    ImageDownloaderTask imageDownloaderTask;
    private long lastSuccessMsecs;

    public CeolDeviceWebSvcMonitor(String baseUrl, int backgroundTimeoutMsecs, int backgroundRateMsecs) {
        ceolDevice = CeolDevice.getInstance();
        this.observers=new ArrayList<OnCeolStatusChangedListener>();
        imageDownloaderTask = new ImageDownloaderTask(this);
        recreateService(baseUrl);
        resetBackgroundCountdown();
        this.backgroundTimeoutMsecs = backgroundTimeoutMsecs;
        this.backgroundRateMsecs = backgroundRateMsecs;
    }

    public void recreateService(String baseUrl) {
        webSvcApiService = WebSvcGenerator.createService(baseUrl);
    }

    public void getStatusSoon() {
        activeThreadUpdater.fireOnce(REPEATONCE_MSECS);
        resetBackgroundCountdown();
    }

    private void startActiveUpdates() {
        if ( activeThreadUpdater == null ) {
            activeThreadUpdater = new UIThreadUpdater(this, REPEATRATE_MSECS);
        }
        activeThreadUpdater.startUpdates();
    }

    private void stopActiveUpdates() {
        if (activeThreadUpdater != null) {
            activeThreadUpdater.stopUpdates();
        }
    }

    public void start() {
        if ( backgroundThreadUpdater == null ) {
            backgroundThreadUpdater = new UIThreadUpdater(this, backgroundRateMsecs);
            backgroundThreadUpdater.startUpdates();
        }
        checkBackgroundUpdate();
    }

    public void getStatus() {
        webSvcApiService.appStatusLiteAsync(new Callback<WebSvcHttpStatusLiteResponse>() {
            @Override
            public void success(WebSvcHttpStatusLiteResponse webSvcHttpStatusLiteResponse, Response response) {
                //Log.d(TAG, "success: Got successful response: " + response.getBody());
                //Log.d(TAG, "success: power: " + webSvcHttpAppCommandResponse.power);
                updateDeviceStatusLite(webSvcHttpStatusLiteResponse);
                getStatus2();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "Could not connect to CEOL: " + error);
                updateDeviceErrorStatus();
                checkBackgroundUpdate();
            }
        });
    }

    public void getStatus2() {
        TypedString statusQuery = determineStatusQuery(ceolDevice.getSIStatus());
        webSvcApiService.appCommandAsync(statusQuery, new Callback<WebSvcHttpAppCommandResponse>() {
            @Override
            public void success(WebSvcHttpAppCommandResponse webSvcHttpAppCommandResponse, Response response) {
                //Log.d(TAG, "success: Got successful response: " + response.getBody());
                //Log.d(TAG, "success: power: " + webSvcHttpAppCommandResponse.power);
                if (webSvcHttpAppCommandResponse.texts == null) {
//                    Log.d(TAG, "success: Hmm - texts is null");
                } else {
                    //Log.d(TAG, "success: title: " + webSvcHttpAppCommandResponse.texts.get("title"));
                    Log.d(TAG, "AppCommand success: ");
                }
                updateDeviceStatus(webSvcHttpAppCommandResponse);
                checkBackgroundUpdate();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "Could not connect to CEOL: " + error);
                updateDeviceErrorStatus();
                checkBackgroundUpdate();
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

        try {
            imageDownloaderTask = new ImageDownloaderTask(this);
            imageDownloaderTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
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
            //case NetServer:
            default:
                return statusQuery_NetServer;
        }
    }


    public void updateDeviceImage(Bitmap bitmap) {
        synchronized (ceolDevice) {
            ceolDevice.NetServer.setImageBitmap(bitmap);
        }
    }

    private void updateDeviceErrorStatus() {
        synchronized (ceolDevice) {
            ceolDevice.setDeviceStatus(DeviceStatusType.Connecting);
//            ceolDevice.setSIStatus(SIStatusType.Unknown);
        }
        notifyObservers();
//        onCeolStatusChangedListener.onCeolStatusChanged(ceolDevice);
    }

    private void updateDeviceStatusLite(WebSvcHttpStatusLiteResponse webSvcHttpStatusLiteResponse) {
        ceolDevice.setDeviceStatus(webSvcHttpStatusLiteResponse.power);
        ceolDevice.setSIStatusLite(webSvcHttpStatusLiteResponse.inputFunc);
        ceolDevice.setIsMuted(webSvcHttpStatusLiteResponse.mute.equals("on"));
    }

    private void updateDeviceStatus(WebSvcHttpAppCommandResponse webSvcHttpAppCommandResponse) {
        SIStatusType oldSiStatus = ceolDevice.getSIStatus();

        String oldTrack = ceolDevice.NetServer.getTrack();
        if (oldTrack == null) {
            Log.d(TAG, "updateDeviceStatus: oldtrack is null");
        }

        try {
            synchronized (ceolDevice) {
                ceolDevice.setSIStatus(webSvcHttpAppCommandResponse.source);
                ceolDevice.setDeviceStatus(webSvcHttpAppCommandResponse.power);
                ceolDevice.setMasterVolume(webSvcHttpAppCommandResponse.dispvalue);

                if ( ceolDevice.isNetServer() ) {

                    CeolDeviceNetServer netServer = ceolDevice.NetServer;
                    ceolDevice.setPlayStatus( webSvcHttpAppCommandResponse.playstatus);

                    Dictionary<WebSvcHttpResponseText> texts = webSvcHttpAppCommandResponse.texts;
                    if (texts != null) {
                        netServer.setIsBrowsing(true);
                        if (webSvcHttpAppCommandResponse.type.equals("browse")) {
                            netServer.initializeEntries(texts.get("title").text,
                                    texts.get("scridValue").text,
                                    texts.get("scrid").text,
                                    webSvcHttpAppCommandResponse.listmax,
                                    webSvcHttpAppCommandResponse.listposition);
                            for (int i = 0; i < CeolDeviceNetServer.MAX_LINES; i++) {
                                WebSvcHttpResponseText responseText = texts.get("line" + i);
                                if (responseText != null) {
                                    netServer.setBrowseLine(i, responseText.text, responseText.flag);
                                }
                            }
                        }
                        if ( ceolDevice.getPlayStatus() == PlayStatusType.Stop) {
                            netServer.setTrackInfo("","","","", "");
                            netServer.setImageBitmap(null);
                        } else {
                            if (webSvcHttpAppCommandResponse.type.equals("play")) {
                                netServer.setTrackInfo(
                                        texts.get("track").text,
                                        texts.get("artist").text,
                                        texts.get("album").text,
                                        texts.get("format").text,
                                        texts.get("bitrate").text
                                );
                                netServer.setIsBrowsing(false);
                            }
                        }
                    } else {
                        netServer.clear();
                    }

                } else {
                    switch (ceolDevice.getSIStatus()) {

                        case Unknown:
                            break;
                        case CD:
                            // TODO
                            break;
                        case Tuner:
                            CeolDeviceTuner tuner = ceolDevice.Tuner;
                            ceolDevice.setPlayStatus(PlayStatusType.Stop);
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

        if ( !imageDownloaderTask.isRunning() && (oldTrack == null || !ceolDevice.NetServer.getTrack().equalsIgnoreCase(oldTrack))) {
            getImage();
        }

//        onCeolStatusChangedListener.onCeolStatusChanged(ceolDevice);
        notifyObservers();
//        if ( ceol.getSIStatus() != oldSiStatus) {
//        }
    }

    @Override
    public void run() {
        getStatus();
    }

    @Override
    public void register(OnCeolStatusChangedListener obj) {
        if(obj == null) return;  // Ignore null observers
        synchronized (MUTEX) {
            if(!observers.contains(obj)) observers.add(obj);
        }
        if ( observers.size() == 1 ) {
            startActiveUpdates();
        }
    }

    @Override
    public void unregister(OnCeolStatusChangedListener obj) {
        if (obj == null) return;
        synchronized (MUTEX) {
            observers.remove(obj);
        }
        if ( observers.size() == 0 ) {
            stopActiveUpdates();
        }
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

}
