package com.candkpeters.ceol.view;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.chris.ceol.R;

/**
 * Created by crisp on 14/02/2016.
 */
public class CeolService_Old extends IntentService {

    private static final String TAG = "CeolService_Old";
    public static final String EXECUTE_COMMAND = "ExecuteCommand";
//    public static final String EXECUTE_COMMAND_NAME = "ExecuteCommandName";
//    public static final String EXECUTE_COMMAND_VALUE = "ExecuteCommandValue";
    private Prefs prefs;
    final Context context = this;

    CeolCommandManager ceolCommandManager = null;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CeolService_Old(String name) {
        super(name);
    }

    public CeolService_Old() {
        this("CeolService_Old");
    }

    OnCeolStatusChangedListener onCeolStatusChangedListener = new OnCeolStatusChangedListener() {
        @Override
        public void onCeolStatusChanged(CeolDevice ceolDevice) {

            AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(context);
            ComponentName myWidget = new ComponentName(context, CeolRemoteWidgerProvider.class);

            for (int widgetId : appWidgetMan.getAppWidgetIds(myWidget)) {

                RemoteViews views = CeolRemoteWidgerProvider.buildRemoteView(context, widgetId);
//                RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.ceolremote_appwidget);

                long curr = System.currentTimeMillis();
                views.setTextViewText(R.id.textUpdate, Long.toString(curr));
//                views.setTextViewText(R.id.textUpdate, "Here we are!");
//                appWidgetMan.updateAppWidget(widgetId, views);
                CeolRemoteWidgerProvider.pushWidgetUpdate(context, views);

            }
        }
    };
/*
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
*/

    @Override
    protected void onHandleIntent(Intent intent) {
        initializeService();

        Log.d(TAG, "onStartCommand: Got here");
        Log.i(TAG, "This is the intent " + intent);
        if (intent != null){
            String requestedAction = intent.getAction();
            Log.i(TAG, "This is the action " + requestedAction);
            if (requestedAction != null && requestedAction.equals(EXECUTE_COMMAND)){

                int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);

                Log.i(TAG, "Package is " + this.getPackageName() + " and the widget is " + widgetId);

                AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(this);

                Command command = CeolIntentFactory.newInstance(intent);

                if ( command != null ) {
                    ceolCommandManager.execute(command);
                }

                RemoteViews views = CeolRemoteWidgerProvider.buildRemoteView(this, widgetId);
//                RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.ceolremote_appwidget);

                long curr = System.currentTimeMillis();
                views.setTextViewText(R.id.textUpdate, Long.toString(curr));
//                views.setTextViewText(R.id.textUpdate, "Here we are!");
//                appWidgetMan.updateAppWidget(widgetId, views);
                CeolRemoteWidgerProvider.pushWidgetUpdate(this, views);
            }
        }

    }

/*
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        initializeService();

        Log.d(TAG, "onStartCommand: Got here");
        Log.i(TAG, "This is the intent " + intent);
        if (intent != null){
            String requestedAction = intent.getAction();
            Log.i(TAG, "This is the action " + requestedAction);
            if (requestedAction != null && requestedAction.equals(EXECUTE_COMMAND)){

                int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);

                Log.i(TAG, "Package is " + this.getPackageName() + " and the widget is " + widgetId);

                AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(this);

                Command command = Command.newInstance(intent);

                if ( command != null ) {
                    ceolCommandManager.execute(command);
                }

                RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.ceolremote_appwidget);

                long curr = System.currentTimeMillis();
                views.setTextViewText(R.id.textUpdate, Long.toString(curr));
//                views.setTextViewText(R.id.textUpdate, "Here we are!");
//                appWidgetMan.updateAppWidget(widgetId, views);
                CeolRemoteWidgerProvider.pushWidgetUpdate(this, views);

            }
        }


//        stopSelf(startId);

        return START_STICKY;
    }
*/

    private void initializeService() {
        // TODO Preferences
        this.prefs = new Prefs(this);
        String baseurl = prefs.getBaseUrl();

//        ceolWebService = new CeolDeviceWebSvcMonitor(baseurl);
//        ceolDevice = CeolDevice.getInstance();
        final Context context = this;
        ceolCommandManager = CeolCommandManager.getInstance();
        ceolCommandManager.setDevice(CeolDevice.getInstance(), baseurl, prefs.getMacroNames(), prefs.getMacroValues());
        ceolCommandManager.register(new OnCeolStatusChangedListener() {
            @Override
            public void onCeolStatusChanged(CeolDevice ceolDevice) {

                AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(context);
                ComponentName myWidget = new ComponentName(context, CeolRemoteWidgerProvider.class);

                for (int widgetId : appWidgetMan.getAppWidgetIds(myWidget)) {

                    RemoteViews views = CeolRemoteWidgerProvider.buildRemoteView(context, widgetId);
//                RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.ceolremote_appwidget);

                    long curr = System.currentTimeMillis();
                    views.setTextViewText(R.id.textUpdate, Long.toString(curr));
//                views.setTextViewText(R.id.textUpdate, "Here we are!");
//                appWidgetMan.updateAppWidget(widgetId, views);
                    CeolRemoteWidgerProvider.pushWidgetUpdate(context, views);

                }
            }
        });

//        ceolCommandManager.register(onCeolStatusChangedListener);

    }
}
