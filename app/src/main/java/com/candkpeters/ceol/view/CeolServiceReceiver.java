package com.candkpeters.ceol.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
            default:
                break;
        }
        context.startService(i);
    }
}
