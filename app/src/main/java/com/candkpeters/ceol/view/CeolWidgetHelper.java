package com.candkpeters.ceol.view;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.candkpeters.ceol.controller.CeolWidgetController;
import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.device.command.Command;

/**
 * Created by crisp on 22/03/2016.
 */
public abstract class CeolWidgetHelper extends AppWidgetProvider {

    private static final String TAG = "WidgetProvider";
    private Prefs prefs;

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

        updateWidgetsFirstTime(context, "Widget updated");
//TODO Start service?
    }

    protected PendingIntent createPendingIntent(Context context, int appWidgetId, Intent intent) {
//        Intent intent = new Intent(context, CeolService_Old.class);
        intent.setClass(context, CeolService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getService(context, 0, intent, 0);
    }

    protected void setOnClickIntent(Context context, int appWidgetId, RemoteViews views, int resId, Command command) {
        PendingIntent clickPendingIntent = createPendingIntent(context, appWidgetId, CeolIntentFactory.getIntent(command));
        views.setOnClickPendingIntent(resId, clickPendingIntent);
    }

    public void startService(Context context, int appWidgetId) {
        Intent intent = new Intent();
        intent.setAction(CeolService.START_SERVICE);
        intent.setClass(context, CeolService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        context.startService(intent);
    }

    protected abstract RemoteViews buildRemoteView(Context context, int widgetId);

    protected void updateWidgetsFirstTime(Context context, String text) {
        AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(context);
        ComponentName myWidget = getComponentName(context);

        for (int widgetId : appWidgetMan.getAppWidgetIds(myWidget)) {

            RemoteViews views =
                    buildRemoteView(context, widgetId);
            if ( views != null ) {
                updateViewsFirstTime(context, views, text);
                pushWidgetUpdate(context, views);
            }
        }
    }

    protected abstract void updateViewsFirstTime(Context context, RemoteViews views, String text);
    
    public void updateWidgets(CeolCommandManager ceolCommandManager, Context context, String text) {

        AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(context);
        ComponentName myWidget = getComponentName(context);

        for (int widgetId : appWidgetMan.getAppWidgetIds(myWidget)) {

            RemoteViews views =
                buildRemoteView(context, widgetId);
            if ( views != null ) {
                updateViews(views, ceolCommandManager, context, text);
                pushWidgetUpdate(context, views);
            }
        }
    }

    protected abstract void updateViews(RemoteViews views, CeolCommandManager ceolCommandManager, Context context, String text);

    protected void pushWidgetUpdate(Context context, RemoteViews views) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(getComponentName(context), views);
    }

    abstract public ComponentName getComponentName(Context context);
    
}
