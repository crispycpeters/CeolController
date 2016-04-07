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
    private static final int BACKGROUNDRATE_MSECS = 60000;
    private static final int REPEATONCE_MSECS = 600;

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
            " <cmd id=\"3\">GetMuteStatus</cmd>\n" +
            " <cmd id=\"4\">GetSourceStatus</cmd>\n" +
            " <cmd id=\"5\">GetNetAudioStatus</cmd>\n" +
            "</tx>\n";
    TypedString statusQuery_NetServer = new TypedString(statusQueryString_NetServer);
    private final static String statusQueryString_CD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<tx>\n" +
            " <cmd id=\"1\">GetPowerStatus</cmd>\n" +
            " <cmd id=\"2\">GetVolumeLevel</cmd>\n" +
            " <cmd id=\"3\">GetMuteStatus</cmd>\n" +
            " <cmd id=\"4\">GetSourceStatus</cmd>\n" +
            " <cmd id=\"5\">GetCDStatus</cmd>\n" +
            "</tx>\n";
    TypedString statusQuery_CD = new TypedString(statusQueryString_CD);
    private final static String statusQueryString_Tuner = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<tx>\n" +
            " <cmd id=\"1\">GetPowerStatus</cmd>\n" +
            " <cmd id=\"2\">GetVolumeLevel</cmd>\n" +
            " <cmd id=\"3\">GetMuteStatus</cmd>\n" +
            " <cmd id=\"4\">GetSourceStatus</cmd>\n" +
            " <cmd id=\"5\">GetTunerStatus</cmd>\n" +
            "</tx>\n";
    TypedString statusQuery_Tuner = new TypedString(statusQueryString_Tuner);
    ImageDownloaderTask imageDownloaderTask;

    public CeolDeviceWebSvcMonitor(String baseUrl) {
        ceolDevice = CeolDevice.getInstance();
        this.observers=new ArrayList<OnCeolStatusChangedListener>();
        imageDownloaderTask = new ImageDownloaderTask(this);
        recreateService(baseUrl);
    }

    public void recreateService(String baseUrl) {
        webSvcApiService = WebSvcGenerator.createService(baseUrl);
    }

    public void getStatusSoon() {
        activeThreadUpdater.fireOnce(REPEATONCE_MSECS);
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
            backgroundThreadUpdater = new UIThreadUpdater(this, BACKGROUNDRATE_MSECS);
            backgroundThreadUpdater.startUpdates();
        }
    }

    public void getStatus() {
        TypedString statusQuery = determineStatusQuery(ceolDevice.getSIStatus());
        webSvcApiService.appCommandAsync(statusQuery, new Callback<WebSvcHttpResponse>() {
            @Override
            public void success(WebSvcHttpResponse webSvcHttpResponse, Response response) {
                //Log.d(TAG, "success: Got successful response: " + response.getBody());
                //Log.d(TAG, "success: power: " + webSvcHttpResponse.power);
                if (webSvcHttpResponse.texts == null) {
//                    Log.d(TAG, "success: Hmm - texts is null");
                } else {
                    //Log.d(TAG, "success: title: " + webSvcHttpResponse.texts.get("title"));
                    logResponse(webSvcHttpResponse);
                }
                updateDeviceStatus(webSvcHttpResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "Could not connect to CEOL: " + error);
                updateDeviceErrorStatus();
                return;
            }
        });
    }

    public void getImage() {

        try {
            imageDownloaderTask = new ImageDownloaderTask(this);
            imageDownloaderTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logResponse(WebSvcHttpResponse webSvcHttpResponse) {
        //Log.d(TAG, "logResponse: " + webSvcHttpResponse.toString());
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
            ceolDevice.setSIStatus(SIStatusType.Unknown);
        }
        notifyObservers();
//        onCeolStatusChangedListener.onCeolStatusChanged(ceolDevice);
    }

    private void updateDeviceStatus(WebSvcHttpResponse webSvcHttpResponse) {
        SIStatusType oldSiStatus = ceolDevice.getSIStatus();

        String oldTrack = ceolDevice.NetServer.getTrack();
        if (oldTrack == null) {
            Log.d(TAG, "updateDeviceStatus: oldtrack is null");
        }

        try {
            synchronized (ceolDevice) {
                ceolDevice.setSIStatus(parseSIStatus(webSvcHttpResponse));
                ceolDevice.setDeviceStatus(webSvcHttpResponse.power);
                ceolDevice.setMasterVolume(webSvcHttpResponse.dispvalue);
                ceolDevice.setIsMuted(webSvcHttpResponse.mute.equals("on"));

                switch (ceolDevice.getSIStatus()) {

                    case Unknown:
                        break;
                    case CD:
                        // TODO
                        break;
                    case Tuner:
                        CeolDeviceTuner tuner = ceolDevice.Tuner;
                        ceolDevice.setPlayStatus(PlayStatusType.Stop);
                        tuner.setBand(webSvcHttpResponse.band);
                        tuner.setFrequency(webSvcHttpResponse.frequency);
                        tuner.setName(webSvcHttpResponse.name);
                        tuner.setIsAuto(webSvcHttpResponse.automanual==null?false:webSvcHttpResponse.automanual.equalsIgnoreCase("AUTO"));
                        break;
                    case IRadio:
                        // TODO - Like NetServer
                        break;
                    case NetServer:
                        CeolDeviceNetServer netServer = ceolDevice.NetServer;
                        ceolDevice.setPlayStatus( webSvcHttpResponse.playstatus);

                        Dictionary<WebSvcHttpResponseText> texts = webSvcHttpResponse.texts;
                        if (texts != null) {
                            netServer.setIsBrowsing(true);
                            if (webSvcHttpResponse.type.equals("browse")) {
                                netServer.initializeEntries(texts.get("title").text,
                                        texts.get("scridValue").text,
                                        texts.get("scrid").text,
                                        webSvcHttpResponse.listmax,
                                        webSvcHttpResponse.listposition);
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
                                if (webSvcHttpResponse.type.equals("play")) {
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
                        break;
                    case AnalogIn:
                        break;
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

    private SIStatusType parseSIStatus(WebSvcHttpResponse webSvcHttpResponse) {
        if ( webSvcHttpResponse.source != null) {
            switch (webSvcHttpResponse.source) {
                case "Music Server":
                    return SIStatusType.NetServer;
                case "TUNER":
                    return SIStatusType.Tuner;
                case "CD":
                    return SIStatusType.CD;
                case "Internet Radio":
                    return SIStatusType.IRadio;
                case "USB":
                    return SIStatusType.Ipod;
                case "ANALOGIN":
                    return SIStatusType.AnalogIn;
                case "DIGITALIN1":
                    return SIStatusType.DigitalIn1;
                case "DIGITALIN2":
                    return SIStatusType.DigitalIn2;
                case "BLUETOOTH":
                    return SIStatusType.Bluetooth;
                default:
                    return SIStatusType.Unknown;
            }
        } else {
            return SIStatusType.Unknown;
        }
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
