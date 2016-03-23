package com.candkpeters.ceol.controller;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.RemoteViews;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.device.command.Command;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.view.CeolIntentFactory;
import com.candkpeters.ceol.view.CeolService;
import com.candkpeters.ceol.view.CeolWidgetHelper;
import com.candkpeters.ceol.view.CeolWidgetHelperNavigator;
import com.candkpeters.ceol.view.CeolWidgetHelperPlayer;
import com.candkpeters.ceol.view.CeolWidgetProviderNavigator;
import com.candkpeters.ceol.view.CeolWidgetProviderPlayer;
import com.candkpeters.ceol.view.Prefs;
import com.candkpeters.chris.ceol.R;

/**
 * Created by crisp on 21/03/2016.
 */
public class CeolWidgetController {
    private static final String TAG = "WidgetController";
    CeolWidgetHelper[] ceolWidgetHelpers = {new CeolWidgetHelperNavigator(), new CeolWidgetHelperPlayer()};

    private Prefs prefs;
    CeolCommandManager ceolCommandManager = null;
    CeolDevice ceolDevice = null;
    OnCeolStatusChangedListener onCeolStatusChangedListener = new OnCeolStatusChangedListener() {
        @Override
        public void onCeolStatusChanged(CeolDevice ceolDevice) {
            updateWidgets(null);
        }
    };
    private String updateString = "";
    final Context context;

    public CeolWidgetController(CeolService ceolService) {
        context = ceolService;
    }

    public void initialize() {
        this.prefs = new Prefs(context);
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
        ceolCommandManager.unregister(onCeolStatusChangedListener);
    }

    private void updateWidgets(String text) {
        updateString = text==null?updateString:text;

        for ( CeolWidgetHelper ceolWidgetHelper : ceolWidgetHelpers) {
            ceolWidgetHelper.updateWidgets(ceolCommandManager, context, text);
        }
    }

    public void executeCommand(int widgetId, AppWidgetManager appWidgetMan, Intent intent) {
        Command command = CeolIntentFactory.newInstance(intent);
        AppWidgetProviderInfo appWidgetProviderInfo = appWidgetMan.getAppWidgetInfo(widgetId);
        Log.d(TAG, "onStartCommand: Provider: " + appWidgetProviderInfo.provider);
        if (command != null) {
            ceolCommandManager.execute(command);
            updateWidgets(command.toString());
        } else {
            updateWidgets("No command");
        }
    }

    public void executeScreenOff() {
        stopDeviceUpdates();
        updateWidgets("Screen off");
    }

    public void executeScreenOn() {
        startDeviceUpdates();
        updateWidgets("Screen on");
    }

    public void executeConfigChanged() {
        updateWidgets("Config changed");
    }

    public void destroy() {
        ceolCommandManager.unregister(onCeolStatusChangedListener);
    }
}
