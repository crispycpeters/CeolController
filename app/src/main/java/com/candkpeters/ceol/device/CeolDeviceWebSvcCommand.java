package com.candkpeters.ceol.device;

import android.util.Log;

import com.candkpeters.ceol.model.CeolDevice;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by crisp on 08/01/2016.
 */
public class CeolDeviceWebSvcCommand {

    private static final String TAG = "CeolDeviceWebSvcMonitor";

    private WebSvcApiService webSvcApiService = null;
    private CeolDevice ceolDevice;

    OnCeolCommandListener onCeolCommandListener;

    public interface OnCeolCommandListener {
        void success();
        void failure();
    }

    public CeolDeviceWebSvcCommand(String baseUrl) {
        recreateService(baseUrl);
    }

    public void recreateService(String baseUrl) {
        webSvcApiService = WebSvcGenerator.createService(baseUrl);
    }

    public void SendCommand( String value, final OnCeolCommandListener onCeolCommandListener) {

        webSvcApiService.appDirectCommandAsync("?" + value, new Callback<Void>() {
            @Override
            public void success(Void responseStr, Response response) {
                Log.d(TAG, "success: SendCommand worked successful response: " + response.getBody());
                if (onCeolCommandListener != null) onCeolCommandListener.success();
            }

            @Override
            public void failure(RetrofitError error) {
                Response response = error.getResponse();
                if (response != null && response.getStatus() == 200) {
                    // It worked anyway - probably due to trying to parse zero-length response
                    if (onCeolCommandListener != null) onCeolCommandListener.success();
                    return;
                } else {
                    Log.e(TAG, "failure: error: " + error);
                    if (onCeolCommandListener!=null) onCeolCommandListener.failure();
                }
            }
        });
    }
}
