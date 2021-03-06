package com.candkpeters.ceol.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.content.ComponentName;

import com.candkpeters.ceol.device.CeolManager;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.service.CeolService;
import com.candkpeters.ceol.view.CeolIntentFactory;
import com.candkpeters.ceol.view.MainActivity;
import com.candkpeters.ceol.view.Prefs;

/**
 * Created by crisp on 22/03/2016.
 */
public abstract class CeolWidgetHelper /*extends AppWidgetProvider*/ {

    private static final String TAG = "WidgetHelper";
    private Prefs prefs;
    boolean isWaiting = false;

    private boolean bound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.d(TAG, "onServiceConnected: Connected to CeolService");
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "onServiceDisconnected: Disconnected from CeolService");
            bound = false;
        }
    };

    private boolean isBound() {
        return bound;
    }

    public void ensureServiceStarted(Context context) {
        if (!bound) {
            Log.d(TAG, "create: Binding");
            Intent intent = new Intent(context, CeolService.class);
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    // On creation of first widget
/*
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }
*/

/*
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        Log.d(TAG, "onUpdate: Entering");
        if ( prefs == null ) {
            this.prefs = new Prefs(context);
        }

        updateWidgetsFirstTime(context, "Widget updated");
        startUpdates(context,0);
    }
*/

    protected PendingIntent createPendingIntent(Context context, int appWidgetId, Intent intent) {
        //intent.setClass(context, CeolService.class);
        intent.setClass(context, CeolWidgetProviderMiniPlayer.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        // TODO: Test with local intent first
        return PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        return PendingIntent.getService(context, 0, intent, 0);
    }

    protected void setOnClickCommandIntent(Context context, int appWidgetId, RemoteViews views, int resId, Command command) {
        PendingIntent clickPendingIntent = createPendingIntent(context, appWidgetId, CeolIntentFactory.getIntent(command));
        views.setOnClickPendingIntent(resId, clickPendingIntent);
    }

    protected void setOnClickAppIntent(Context context, int appWidgetId, RemoteViews views, int resId) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        MainActivity a = new MainActivity();
        ComponentName cn = new ComponentName(context, CeolService.class);
        intent.setComponent(cn);

        PendingIntent clickPendingIntent = createPendingIntent(context, appWidgetId, intent);
        views.setOnClickPendingIntent(resId, clickPendingIntent);
    }

/*
    public void startUpdates(Context context, int appWidgetId) {
        Intent intent = new Intent();
        intent.setAction(CeolService.START_SERVICE);
        intent.setClass(context, CeolService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        context.startUpdates(intent);
    }
*/

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

    public boolean widgetsExist(CeolManager ceolManager, Context context) {
        AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(context);
        ComponentName myWidget = getComponentName(context);

        return appWidgetMan.getAppWidgetIds(myWidget).length > 0;
    }

    public void updateWidgets(CeolManager ceolManager, Context context, String text) {

        AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(context);
        ComponentName myWidget = getComponentName(context);

        for (int widgetId : appWidgetMan.getAppWidgetIds(myWidget)) {

            RemoteViews views =
                    buildRemoteView(context, widgetId);
            if ( views != null ) {
                updateViews(views, ceolManager, context, text);
                pushWidgetUpdate(context, views);
            }
        }
    }

    public void updateWidgetsOnDestroy(CeolManager ceolManager, Context context) {

        AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(context);
        ComponentName myWidget = getComponentName(context);

        for (int widgetId : appWidgetMan.getAppWidgetIds(myWidget)) {

            RemoteViews views =
                    buildRemoteView(context, widgetId);
            if ( views != null ) {
                updateViewsOnDestroy(views, ceolManager, context);
                pushWidgetUpdate(context, views);
            }
        }
    }

    protected abstract void updateViews(RemoteViews views, CeolManager ceolManager, Context context, String text);

    protected abstract void updateViewsOnDestroy(RemoteViews views, CeolManager ceolManager, Context context);

    protected void pushWidgetUpdate(Context context, RemoteViews views) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(getComponentName(context), views);
    }

    abstract public ComponentName getComponentName(Context context);

    public void setWaiting(boolean b) {
        isWaiting = b;
    }
}
