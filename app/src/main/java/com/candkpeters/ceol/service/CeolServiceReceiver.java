package com.candkpeters.ceol.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;

import com.candkpeters.ceol.service.CeolService;

/**
 * Created by crisp on 17/03/2016.
 */
public class CeolServiceReceiver extends BroadcastReceiver {
    private static final String TAG = "CeolServiceReceiver";

    private boolean screenOff;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentForService = new Intent(context, CeolService.class);
        switch ( intent.getAction()) {
            case Intent.ACTION_SCREEN_OFF:
                intentForService.setAction(CeolService.SCREEN_OFF);
                break;
            case Intent.ACTION_SCREEN_ON:
                intentForService.setAction(CeolService.SCREEN_ON);
                break;
            case Intent.ACTION_CONFIGURATION_CHANGED:
                intentForService.setAction(CeolService.CONFIG_CHANGED);
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                intentForService.setAction(CeolService.BOOT_COMPLETED);
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                 intentForService.setAction(CeolService.CONNECTIVITY_ACTION);
                break;
            default:
                // Pass through whatever we have - it will likely be a command
                intentForService = intent;
                intentForService.setClass(context, CeolService.class);
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentForService);
        } else {
            context.startService(intentForService);
        }
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
