package com.candkpeters.ceol.view;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.RemoteViews;

import com.candkpeters.ceol.controller.CeolWidgetController;
import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.chris.ceol.R;

/**
 * Created by crisp on 14/02/2016.
 */
public class CeolService extends Service {

    private static final String TAG = "CeolService";
    public static final String EXECUTE_COMMAND = "ExecuteCommand";
    public static final String SCREEN_OFF = "ScreenOff";
    public static final String SCREEN_ON = "ScreenOn";
    public static final String CONFIG_CHANGED = "ConfigChanged";
    public static final String START_SERVICE = "StartService";
    public static final String BOOT_COMPLETED = "BootCompleted";
    //    public static final String EXECUTE_COMMAND_NAME = "ExecuteCommandName";
//    public static final String EXECUTE_COMMAND_VALUE = "ExecuteCommandValue";
    private Prefs prefs;
    final Context context = this;

    CeolCommandManager ceolCommandManager = null;
    CeolDevice ceolDevice = null;

    CeolWidgetController ceolWidgetController;

    public CeolService() {
        ceolWidgetController = new CeolWidgetController(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
    * On first creation
    * Start up the service in background mode
    */
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: Entering");
        initializeService();

        // register receiver that handles various events
        CeolServiceReceiver mReceiver = new CeolServiceReceiver();
        registerReceiver(mReceiver, mReceiver.createIntentFilter());
    }

    /*
    * On starting service
    *
    */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand: Got here");
        Log.i(TAG, "This is the intent " + intent);
        if (intent != null){
            String requestedAction = intent.getAction();
            Log.i(TAG, "This is the action " + requestedAction);
            if (requestedAction != null) {
                switch( requestedAction) {
                    case START_SERVICE:
                        Log.d(TAG, "onStartCommand: START_SERVICE");
                        ceolWidgetController.startService();
                        break;
                    case EXECUTE_COMMAND:
                        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
                        Log.i(TAG, "Package is " + this.getPackageName() + " and the widget is " + widgetId);
                        AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(this);
                        ceolWidgetController.executeCommand(widgetId, appWidgetMan, intent);
                        break;
                    case SCREEN_OFF:
                        Log.d(TAG, "onStartCommand: SCREEN_OFF");
                        ceolWidgetController.executeScreenOff();
                        break;
                    case SCREEN_ON:
                        Log.d(TAG, "onStartCommand: SCREEN_ON");
                        ceolWidgetController.executeScreenOn();
                        break;
                    case CONFIG_CHANGED:
                        Log.d(TAG, "onStartCommand: CONFIG_CHANGED");
                        ceolWidgetController.executeConfigChanged();
                        break;
                    case BOOT_COMPLETED:
                        Log.d(TAG, "onStartCommand: BOOT_COMPLETED");
                        ceolWidgetController.executeBootCompleted();
                        break;
                    default:
                        break;
                }
            }
        } else {
            // Service was restarted
            Log.d(TAG, "onStartCommand: Restarting service - no Intent");
            ceolWidgetController.startService();
        }

        return START_STICKY;    // Try to restart if service has to be destroyed
    }

    @Override
    public void onDestroy() {
        ceolWidgetController.destroy();
    }

    private void initializeService() {
        ceolWidgetController.initialize();
    }

}
