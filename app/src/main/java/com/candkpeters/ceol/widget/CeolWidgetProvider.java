package com.candkpeters.ceol.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.candkpeters.ceol.service.CeolService;
import com.candkpeters.ceol.view.Prefs;
import com.candkpeters.ceol.widget.CeolWidgetHelper;

/**
 * Created by crisp on 22/03/2016.
 */
public abstract class CeolWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "WidgetProvider";
    private Prefs prefs;
    protected CeolWidgetHelper ceolWidgetHelper;



    public CeolWidgetProvider(CeolWidgetHelper ceolWidgetHelper) {
        this.ceolWidgetHelper = ceolWidgetHelper;
    }


    // On creation of first widget
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        Log.d(TAG, "onUpdate: Entering");
        if ( prefs == null ) {
            this.prefs = new Prefs(context);
        }

//        startService(context, 0);
//        ensureServiceStarted(context);
        ceolWidgetHelper.updateWidgetsFirstTime(context, "Widget updated");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        intent.setClass(context, CeolService.class);
//        context.startService(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        Log.d(TAG, "onReceive: Got an intent... Do the service thing...");
    }

    private void ensureServiceStarted(Context context) {
        ceolWidgetHelper.ensureServiceStarted(context);
    }

    public void startService(Context context, int appWidgetId) {
        Intent intent = new Intent();
        intent.setAction(CeolService.START_SERVICE);
        intent.setClass(context, CeolService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        context.startService(intent);
    }

}
