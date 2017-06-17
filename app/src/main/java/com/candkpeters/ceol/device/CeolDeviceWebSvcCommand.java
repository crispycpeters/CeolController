package com.candkpeters.ceol.device;

import android.util.Log;

import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.view.Prefs;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by crisp on 08/01/2016.
 */
class CeolDeviceWebSvcCommand {

    private static final String TAG = "CeolDeviceWebSvcCommand";

    private WebSvcApiService webSvcApiService = null;

    interface OnCeolCommandListener {
        void success();
        void failure();
    }

    CeolDeviceWebSvcCommand(CeolModel ceolModel) {

    }

    private void recreateService(String baseUrl) {
        webSvcApiService = WebSvcGenerator.createService(baseUrl);
    }

    void sendCeolCommand(String value, final OnCeolCommandListener onCeolCommandListener) {

        webSvcApiService.appDirectCommandAsync("?" + value, new Callback<Void>() {
            @Override
            public void success(Void responseStr, Response response) {
                Log.d(TAG, "success: sendCeolCommand worked successful response: " + response.getBody());
                if (onCeolCommandListener != null) onCeolCommandListener.success();
            }

            @Override
            public void failure(RetrofitError error) {
                Response response = error.getResponse();
                if (response != null && response.getStatus() == 200) {
                    // It worked anyway - probably due to trying to parse zero-length response
                    if (onCeolCommandListener != null) onCeolCommandListener.success();
                } else {
                    Log.e(TAG, "failure: error: " + error);
                    if (onCeolCommandListener!=null) onCeolCommandListener.failure();
                }
            }
        });
    }

    public void start( Prefs prefs) {
        recreateService(prefs.getBaseUrl());
    }

    public void stop() {
    }
}
