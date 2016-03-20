package com.candkpeters.ceol.view;

import android.app.Service;
import android.appwidget.AppWidgetManager;
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
//    public static final String EXECUTE_COMMAND_NAME = "ExecuteCommandName";
//    public static final String EXECUTE_COMMAND_VALUE = "ExecuteCommandValue";
    private Prefs prefs;
    final Context context = this;

    CeolCommandManager ceolCommandManager = null;
    CeolDevice ceolDevice = null;

    public CeolService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    OnCeolStatusChangedListener onCeolStatusChangedListener = new OnCeolStatusChangedListener() {
        @Override
        public void onCeolStatusChanged(CeolDevice ceolDevice) {
            updateWidgets(null);
        }
    };

    @Override
    public void onCreate() {
        initializeService();
// register receiver that handles screen on and screen off logic
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand: Got here");
        Log.i(TAG, "This is the intent " + intent);
        if (intent != null){
            String requestedAction = intent.getAction();
            Log.i(TAG, "This is the action " + requestedAction);
            if (requestedAction != null) {
                switch( requestedAction) {
                    case EXECUTE_COMMAND:

                        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
                        Log.i(TAG, "Package is " + this.getPackageName() + " and the widget is " + widgetId);
                        AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(this);
                        Command command = CeolIntentFactory.newInstance(intent);

                        if (command != null) {
                            ceolCommandManager.execute(command);
                        }

                        updateWidgets(command.toString());
                        break;
                    case SCREEN_OFF:
                        stopDeviceUpdates();
                        updateWidgets("Screen off");
                        break;
                    case SCREEN_ON:
                        startDeviceUpdates();
                        updateWidgets("Screen on");
                        break;
                    default:
                        break;
                }
            }
        }
        return START_STICKY;
    }

    private String updateString = "";

    private void updateWidgets(String text) {
        updateString = text==null?updateString:text;
        AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(context);
        ComponentName myWidget = new ComponentName(context, CeolRemoteWidgerProvider.class);

        for (int widgetId : appWidgetMan.getAppWidgetIds(myWidget)) {

            RemoteViews views = CeolRemoteWidgerProvider.buildRemoteView(context, widgetId);

            updateViews(views);
            CeolRemoteWidgerProvider.pushWidgetUpdate(context, views);
        }
    }

    private void updateViews(RemoteViews views) {
        long curr = System.currentTimeMillis();
        views.setTextViewText(R.id.textUpdate, updateString + ": " + Long.toString(curr % 10000));

        views.setTextViewText(R.id.textTrack, ceolDevice.NetServer.getTrack());
        views.setTextViewText(R.id.textArtist, ceolDevice.NetServer.getArtist());
        views.setTextViewText(R.id.textAlbum, ceolDevice.NetServer.getAlbum());

        setRowView(views, R.id.textRow0, 0);
        setRowView(views, R.id.textRow1, 1);
        setRowView(views, R.id.textRow2, 2);
        setRowView(views, R.id.textRow3, 3);
        setRowView(views, R.id.textRow4, 4);
        setRowView(views, R.id.textRow5, 5);
        setRowView(views, R.id.textRow6, 6);
        setRowView(views, R.id.textRow7, 7);
    }

    private void setRowView(RemoteViews views, int viewId, int rowindex) {
        if (ceolDevice.NetServer.isBrowsing()) {
            SpannableString s = new SpannableString(ceolDevice.NetServer.getEntries().getBrowseLineText(rowindex));
            if ( ceolDevice.NetServer.getEntries().getSelectedEntryIndex() == rowindex) {
                s.setSpan(new StyleSpan(Typeface.BOLD_ITALIC),0, s.length(),0);
            }
            views.setTextViewText(viewId, s);
        } else {
            views.setTextViewText(viewId, "");
        }

    }
    private String getRowText(int row) {
        String text = "";
        if ( ceolDevice.NetServer.getEntries().getSelectedEntryIndex() == row) {
            text += "S: ";
        }
        text += ceolDevice.NetServer.getEntries().getBrowseLineText(row);
        return text;
    }

    @Override
    public void onDestroy() {
        ceolCommandManager.unregister(onCeolStatusChangedListener);
    }

    private void initializeService() {
        this.prefs = new Prefs(this);
        String baseurl = prefs.getBaseUrl();

        ceolCommandManager = CeolCommandManager.getInstance();
        ceolCommandManager.setDevice(CeolDevice.getInstance(), baseurl, prefs.getMacroNames(), prefs.getMacroValues());
        ceolDevice = ceolCommandManager.getCeolDevice();
        startDeviceUpdates();

    }

    private void startDeviceUpdates() {
        ceolCommandManager.register(onCeolStatusChangedListener);
    }

    private void stopDeviceUpdates() {
        ceolCommandManager.register(onCeolStatusChangedListener);
    }

}
