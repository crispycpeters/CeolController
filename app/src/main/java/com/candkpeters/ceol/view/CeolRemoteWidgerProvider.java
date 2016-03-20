package com.candkpeters.ceol.view;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.device.command.CommandControl;
import com.candkpeters.ceol.device.command.CommandControlStop;
import com.candkpeters.ceol.device.command.CommandCursor;
import com.candkpeters.ceol.device.command.CommandCursorEnter;
import com.candkpeters.ceol.device.command.CommandFastBackward;
import com.candkpeters.ceol.device.command.CommandFastForward;
import com.candkpeters.ceol.device.command.CommandMacro;
import com.candkpeters.ceol.device.command.CommandMasterVolumeDown;
import com.candkpeters.ceol.device.command.CommandMasterVolumeUp;
import com.candkpeters.ceol.device.command.CommandSetPower;
import com.candkpeters.ceol.device.command.CommandSetSI;
import com.candkpeters.ceol.device.command.CommandSkipBackward;
import com.candkpeters.ceol.device.command.CommandSkipForward;
import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.ceol.model.SIStatusType;
import com.candkpeters.chris.ceol.R;

/**
 * Created by crisp on 12/02/2016.
 */
public class CeolRemoteWidgerProvider extends AppWidgetProvider {

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

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            Log.i(TAG, "Package name " + context.getPackageName());

            RemoteViews views = buildRemoteView(context, appWidgetId);

            updateMacroButtons(views, prefs.getMacroNames());
            views.setTextViewText(R.id.textUpdate, "Widget updated!");

            // You must call updateAppWidget, passing in the widget ID,
            // to "commit" your changes.
            pushWidgetUpdate(context, views);

            startService( context, appWidgetId);
//            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void updateMacroButtons(RemoteViews views, String[] macroNames) {
        if ( macroNames.length > 0) {
            views.setTextViewText(R.id.performMacro1B,macroNames[0]);
        }
        if ( macroNames.length > 1) {
            views.setTextViewText(R.id.performMacro2B,macroNames[1]);
        }
        if ( macroNames.length > 2) {
            views.setTextViewText(R.id.performMacro3B,macroNames[2]);
        }
    }

    private static void setOnClickIntent(Context context, int appWidgetId, RemoteViews views, int resId, Command command) {
        PendingIntent clickPendingIntent = createPendingIntent(context, appWidgetId, CeolIntentFactory.getIntent(command));
        views.setOnClickPendingIntent(resId, clickPendingIntent);
    }

/*
    private static PendingIntent createPendingIntent(Context context, int appWidgetId, String executeCommand) {
        Intent intent = new Intent(context, CeolService_Old.class);
        intent.setAction(CeolService_Old.EXECUTE_COMMAND);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getService(context, 0, intent, 0);
    }
*/

    private void startService(Context context, int appWidgetId) {
        Intent intent = new Intent();
        intent.setAction(CeolService.START_SERVICE);
        intent.setClass(context, CeolService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        context.startService(intent);
    }
    public static void pushWidgetUpdate(Context context, RemoteViews views) {
        ComponentName myWidget = new ComponentName(context, CeolRemoteWidgerProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, views);
    }

    private static PendingIntent createPendingIntent(Context context, int appWidgetId, Intent intent) {
//        Intent intent = new Intent(context, CeolService_Old.class);
        intent.setClass(context, CeolService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getService(context, 0, intent, 0);
    }

    public static RemoteViews buildRemoteView(Context context, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ceolremote_appwidget);
        setOnClickIntent(context, appWidgetId, views, R.id.powerB, new CommandSetPower());
        setOnClickIntent(context, appWidgetId, views, R.id.performMacro1B, new CommandMacro(1));
        setOnClickIntent(context, appWidgetId, views, R.id.performMacro2B, new CommandMacro(2));
        setOnClickIntent(context, appWidgetId, views, R.id.performMacro3B, new CommandMacro(3));
        setOnClickIntent(context, appWidgetId, views, R.id.volumeupB, new CommandMasterVolumeUp());
        setOnClickIntent(context, appWidgetId, views, R.id.volumedownB, new CommandMasterVolumeDown());
        setOnClickIntent(context, appWidgetId, views, R.id.fastBackwardsB, new CommandFastBackward());
        setOnClickIntent(context, appWidgetId, views, R.id.fastForwardsB, new CommandFastForward());
        setOnClickIntent(context, appWidgetId, views, R.id.skipBackwardsB, new CommandSkipBackward());
        setOnClickIntent(context, appWidgetId, views, R.id.skipForwardsB, new CommandSkipForward());
        setOnClickIntent(context, appWidgetId, views, R.id.playpauseB, new CommandControl());
        setOnClickIntent(context, appWidgetId, views, R.id.stopB, new CommandControlStop());
        setOnClickIntent(context, appWidgetId, views, R.id.navLeftB, new CommandCursor(DirectionType.Left));
        setOnClickIntent(context, appWidgetId, views, R.id.navRightB, new CommandCursor(DirectionType.Right));
        setOnClickIntent(context, appWidgetId, views, R.id.navUpB, new CommandCursor(DirectionType.Up));
        setOnClickIntent(context, appWidgetId, views, R.id.navDownB, new CommandCursor(DirectionType.Down));
        setOnClickIntent(context, appWidgetId, views, R.id.navEnterB, new CommandCursorEnter());
        setOnClickIntent(context, appWidgetId, views, R.id.siInternetRadioB, new CommandSetSI(SIStatusType.IRadio));
        setOnClickIntent(context, appWidgetId, views, R.id.siIpodB, new CommandSetSI(SIStatusType.Ipod));
        setOnClickIntent(context, appWidgetId, views, R.id.siMusicServerB, new CommandSetSI(SIStatusType.NetServer));
        setOnClickIntent(context, appWidgetId, views, R.id.siTunerB, new CommandSetSI(SIStatusType.Tuner));
        setOnClickIntent(context, appWidgetId, views, R.id.siAnalogInB, new CommandSetSI(SIStatusType.AnalogIn));
        setOnClickIntent(context, appWidgetId, views, R.id.siDigitalInB, new CommandSetSI(SIStatusType.DigitalIn1));
        setOnClickIntent(context, appWidgetId, views, R.id.siBluetoothB, new CommandSetSI(SIStatusType.Bluetooth));
        setOnClickIntent(context, appWidgetId, views, R.id.siCdB, new CommandSetSI(SIStatusType.CD));

        return views;
    }
}
