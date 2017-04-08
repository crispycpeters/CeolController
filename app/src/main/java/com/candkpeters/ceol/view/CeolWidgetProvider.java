package com.candkpeters.ceol.view;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.candkpeters.ceol.service.CeolService;

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

        startService(context, 0);
        ceolWidgetHelper.updateWidgetsFirstTime(context, "Widget updated");
    }


    public void startService(Context context, int appWidgetId) {
        Intent intent = new Intent();
        intent.setAction(CeolService.START_SERVICE);
        intent.setClass(context, CeolService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        context.startService(intent);
    }


}
