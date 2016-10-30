package com.candkpeters.ceol.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

/**
 * Created by crisp on 17/03/2016.
 */
public class CeolServiceReceiver extends BroadcastReceiver {

    private boolean screenOff;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, CeolService.class);
        switch ( intent.getAction()) {
            case Intent.ACTION_SCREEN_OFF:
                i.setAction(CeolService.SCREEN_OFF);
                break;
            case Intent.ACTION_SCREEN_ON:
                i.setAction(CeolService.SCREEN_ON);
                break;
            case Intent.ACTION_CONFIGURATION_CHANGED:
                i.setAction(CeolService.CONFIG_CHANGED);
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                i.setAction(CeolService.BOOT_COMPLETED);
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
/*
                final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if ( wifi.isConnected()) {
                    i.setAction(CeolService.SCREEN_ON);
                } else {
                    i.setAction(CeolService.SCREEN_OFF);
                }
*/
                break;
            default:
                break;
        }
        context.startService(i);
    }


    public IntentFilter createIntentFilter() {
        // register receiver that handles screen on and screen off logic
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        return filter;
    }

}
