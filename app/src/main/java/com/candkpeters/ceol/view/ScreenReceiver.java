package com.candkpeters.ceol.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by crisp on 17/03/2016.
 */
public class ScreenReceiver extends BroadcastReceiver {

    private boolean screenOff;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, CeolService.class);
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            i.setAction(CeolService.SCREEN_OFF);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            i.setAction(CeolService.SCREEN_ON);
        }
        context.startService(i);
    }
}
