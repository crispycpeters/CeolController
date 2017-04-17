package com.candkpeters.ceol.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.candkpeters.ceol.device.CeolManager;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.device.command.CommandBaseApp;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.view.CeolIntentFactory;
import com.candkpeters.ceol.service.CeolService;
import com.candkpeters.ceol.view.MainActivity;
import com.candkpeters.ceol.view.Prefs;

/**
 * Created by crisp on 21/03/2016.
 */
public class CeolWidgetController {
    private static final String TAG = "WidgetController";
    CeolWidgetHelper[] ceolWidgetHelpers = {
            new CeolWidgetHelperMiniPlayer()
    };

    private Prefs prefs;
    public CeolManager ceolManager = null;
    CeolDevice ceolDevice = null;
    OnCeolStatusChangedListener onCeolStatusChangedListener = new OnCeolStatusChangedListener() {
        @Override
        public void onCeolStatusChanged() {
            updateWidgets(null);
        }
    };
    private String updateString = "";
    final Context context;
    private int commandDepth = 0;

    public CeolWidgetController(CeolService ceolService) {
        context = ceolService;
    }

    public void initialize( CeolManager ceolManager) {
/*
        this.prefs = new Prefs(context);
        String baseUrl = prefs.getBaseUrl();
*/

        this.ceolManager = ceolManager;
//        ceolManager = CeolManager.getInstance();
//        ceolManager.initialize(context);//CeolDevice.getInstance(), baseUrl, prefs.getMacroNames(), prefs.getMacroValues());
        ceolDevice = ceolManager.getCeolDevice();
        ceolManager.start();
        startService();
    }

    private void startWidgetUpdates() {
        ceolManager.register(onCeolStatusChangedListener);
    }

    private void stopWidgetUpdates() {
        ceolManager.unregister(onCeolStatusChangedListener);
    }

    private boolean widgetsExist() {
        for ( CeolWidgetHelper ceolWidgetHelper : ceolWidgetHelpers) {
            if (ceolWidgetHelper.widgetsExist(ceolManager, context))
                return true;
        }
        return false;
    }

    private void updateWidgets(String text) {
        updateString = text==null?updateString:text;

        for ( CeolWidgetHelper ceolWidgetHelper : ceolWidgetHelpers) {
            ceolWidgetHelper.setWaiting(isWaiting());
            ceolWidgetHelper.updateWidgets(ceolManager, context, text);
        }
    }

    public void executeCommand(final int widgetId, final AppWidgetManager appWidgetMan, Intent intent) {

        Command command = CeolIntentFactory.newInstance(intent);
        AppWidgetProviderInfo appWidgetProviderInfo = appWidgetMan.getAppWidgetInfo(widgetId);
        Log.d(TAG, "onStartCommand: Provider: " + appWidgetProviderInfo);
        Log.d(TAG, "onStartCommand: Provider name: " + appWidgetProviderInfo.provider);
        if (command != null) {
            commandStarting(widgetId, appWidgetMan);
            ceolManager.execute(command, new OnCeolStatusChangedListener() {
                @Override
                public void onCeolStatusChanged() {
                    Log.d(TAG, "onCeolStatusChanged: Stop waiting");
                    commandDone(widgetId, appWidgetMan);
                }
            });
            updateWidgets(command.toString());
            if ( command instanceof CommandBaseApp) {
                Intent i = new Intent();
                i.setClass(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(CeolService.START_ACTIVITY_ACTION,((CommandBaseApp) command).getAction());
                context.startActivity(i);
            }
        } else {
            updateWidgets("No command");
        }
    }

    private boolean isWaiting() {
        return commandDepth > 0;
    }

    private void commandStarting(int widgetId, AppWidgetManager appWidgetMan) {
        commandDepth ++;
        if ( isWaiting()) {
            Log.d(TAG, "commandStarting: Send waiting");
            for (CeolWidgetHelper ceolWidgetHelper : ceolWidgetHelpers) {
                ceolWidgetHelper.setWaiting(true);
                ceolWidgetHelper.updateWidgets(ceolManager, context, "Waiting");
            }
        }
    }

    private void commandDone(int widgetId, AppWidgetManager appWidgetMan) {
        commandDepth --;
        if ( !isWaiting()) {
            Log.d(TAG, "commandStarting: Send stop waiting");
            for (CeolWidgetHelper ceolWidgetHelper : ceolWidgetHelpers) {
                ceolWidgetHelper.setWaiting(false);
                ceolWidgetHelper.updateWidgets(ceolManager, context, "Waiting");
            }
        }
    }

    public void executeScreenOff() {
        stopWidgetUpdates();
        updateWidgets("Screen off");
    }

    public void executeScreenOn() {
        startWidgetUpdates();
        updateWidgets("Screen on");
    }

    public void executeConfigChanged() {
        updateWidgets("Config changed");
    }

    public void destroy() {
        stopWidgetUpdates();
    }

    public void executeBootCompleted() {
        startService();
    }

    public void startService() {
        if (widgetsExist()) {
            Log.d(TAG, "startService: Need to start service as widgets exist");
            startWidgetUpdates();
        }
/*
        Notification notification = new Notification()
                Notification(R.drawable.icon, getText(R.string.ticker_text),
                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, ExampleActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, getText(R.string.notification_title),
                getText(R.string.notification_message), pendingIntent);
        startForeground(ONGOING_NOTIFICATION_ID, notification);
*/
    }
}
