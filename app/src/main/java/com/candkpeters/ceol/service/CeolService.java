package com.candkpeters.ceol.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.candkpeters.ceol.device.CeolManager;
import com.candkpeters.ceol.widget.CeolWidgetController;
import com.candkpeters.ceol.view.Prefs;

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
    public static final String START_ACTIVITY_ACTION = "CeolAction";
    public static final String CONNECTIVITY_ACTION = "ConnectivityAction";
    public static final String STOP_CLING = "StopCling";
    public static final String START_CLING = "StartCling";

    private Prefs prefs;
    final Context context = this;

    CeolWidgetController ceolWidgetController;
    private CeolManager ceolManager;

    public CeolService() {
        ceolManager = new CeolManager(context);
        ceolWidgetController = new CeolWidgetController(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: Entering");
//        initializeService();
        return new CeolServiceBinder(this);
    }

    public CeolManager getCeolManager() {
        return ceolManager;
    }

    /*
    * On first creation
    * Start up the service in background mode
    */
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: Entering");

        ceolManager.initialize();
        ceolWidgetController.initialize(ceolManager);
//        ceolManager.startGatherers();

        // register receiver that handles various events
        CeolServiceReceiver mReceiver = new CeolServiceReceiver();
        registerReceiver(mReceiver, mReceiver.createIntentFilter());

    }

    /*
    * On starting service
    */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "This is the intent " + intent);

        if (!ceolManager.isOnWifi()) {
            stopGathering();
            ceolWidgetController.updateWidgets("No wifi");
            return START_NOT_STICKY;
        } else {
            if (intent != null) {
                String requestedAction = intent.getAction();
//            Log.i(TAG, "This is the action " + requestedAction);
                if (requestedAction != null) {
                    switch (requestedAction) {
                        case START_SERVICE:
                            Log.d(TAG, "onStartCommand: START_SERVICE");
                            ceolWidgetController.startUpdates();
                            ceolManager.startGatherers();
                            ceolWidgetController.updateWidgets("Starting");
                            break;
                        case EXECUTE_COMMAND:
                            int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
//                        Log.i(TAG, "Package is " + this.getPackageName() + " and the widget is " + widgetId);
                            AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(this);
                            ceolWidgetController.executeCommand(widgetId, appWidgetMan, intent);
                            ceolWidgetController.startUpdates();
                            break;
                        case SCREEN_OFF:
                            Log.d(TAG, "onStartCommand: SCREEN_OFF");
//                        ceolManager.pauseGatherers();
                            stopGathering();
                            break;
                        case SCREEN_ON:
                            Log.d(TAG, "onStartCommand: SCREEN_ON");
                            startGathering();
                            break;
                        case CONNECTIVITY_ACTION:
                            Log.d(TAG, "onStartCommand: CONNECTIVITY_ACTION");
                            startGathering();
                            break;
                        case CONFIG_CHANGED:
                            Log.d(TAG, "onStartCommand: CONFIG_CHANGED");
                            ceolWidgetController.executeConfigChanged();
                            break;
                        case BOOT_COMPLETED:
                            Log.d(TAG, "onStartCommand: BOOT_COMPLETED");
                            startGathering();
                            break;
                        case STOP_CLING:
                            Log.d(TAG, "onStartCommand: STOP_CLING");
                            ceolManager.stopCling();
                            break;
                        case START_CLING:
                            Log.d(TAG, "onStartCommand: START_CLING");
                            startGathering();
                            break;
                        default:
                            break;
                    }
                }
            } else {
                // Service was restarted
                Log.d(TAG, "onStartCommand: Restarting service - no Intent");
                startGathering();
            }
            ceolWidgetController.updateWidgets("wifi");

            return START_STICKY;    // Try to restart if service has to be destroyed
        }
    }

    private void stopGathering() {

        ceolManager.stopGatherers();
//        ceolWidgetController.executeScreenOff();
    }

    private void startGathering() {
        ceolManager.startGatherers();
        ceolWidgetController.executeScreenOn();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Entering");
        stopGathering();
        ceolWidgetController.destroy();
    }


}
