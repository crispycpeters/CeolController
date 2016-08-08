package com.candkpeters.ceol.view;

import android.content.ComponentName;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.device.command.CommandApp;
import com.candkpeters.ceol.device.command.CommandMacro;
import com.candkpeters.ceol.device.command.CommandSetPowerToggle;
import com.candkpeters.ceol.device.command.CommandSetSI;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.SIStatusType;
import com.candkpeters.chris.ceol.R;

/**
 * Created by crisp on 22/03/2016.
 */
public class CeolWidgetHelperToplevel extends CeolWidgetHelper {

    @Override
    public ComponentName getComponentName(Context context) {
        return new ComponentName(context, CeolWidgetProviderToplevel.class);
    }

    @Override
    protected RemoteViews buildRemoteView(Context context, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout_toplevel);
        setOnClickCommandIntent(context, appWidgetId, views, R.id.powerB, new CommandSetPowerToggle());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.performMacro1B, new CommandMacro(1));
        setOnClickCommandIntent(context, appWidgetId, views, R.id.performMacro2B, new CommandMacro(2));
        setOnClickCommandIntent(context, appWidgetId, views, R.id.performMacro3B, new CommandMacro(3));
        setOnClickCommandIntent(context, appWidgetId, views, R.id.siInternetRadioB, new CommandSetSI(SIStatusType.IRadio));
        setOnClickCommandIntent(context, appWidgetId, views, R.id.siIpodB, new CommandSetSI(SIStatusType.Ipod));
        setOnClickCommandIntent(context, appWidgetId, views, R.id.siMusicServerB, new CommandSetSI(SIStatusType.NetServer));
        setOnClickCommandIntent(context, appWidgetId, views, R.id.siTunerB, new CommandSetSI(SIStatusType.Tuner));
        setOnClickCommandIntent(context, appWidgetId, views, R.id.siAnalogInB, new CommandSetSI(SIStatusType.AnalogIn));
        setOnClickCommandIntent(context, appWidgetId, views, R.id.siDigitalInB, new CommandSetSI(SIStatusType.DigitalIn1));
        setOnClickCommandIntent(context, appWidgetId, views, R.id.siBluetoothB, new CommandSetSI(SIStatusType.Bluetooth));
        setOnClickCommandIntent(context, appWidgetId, views, R.id.siCdB, new CommandSetSI(SIStatusType.CD));
        setOnClickAppIntent(context, appWidgetId, views, R.id.appB);

        return views;
    }

    @Override
    protected void updateViewsFirstTime(Context context, RemoteViews views, String text) {
        updateMacroButtons(context, views);
        views.setTextViewText(R.id.textUpdate, text);
    }

    String updateString = "";

    @Override
    protected void updateViews(RemoteViews views, CeolCommandManager ceolCommandManager, Context context, String text) {
        long curr = System.currentTimeMillis();
        CeolDevice ceolDevice = ceolCommandManager.getCeolDevice();

        views.setViewVisibility(R.id.waitingPB, isWaiting?View.VISIBLE:View.INVISIBLE);
        views.setTextViewText(R.id.textUpdate, updateString + ": " + Long.toString(curr % 10000));

        updateMacroButtons(context, views);

    }

    public void updateMacroButtons(Context context, RemoteViews views) {
        Prefs prefs = new Prefs(context);
        String[] macroNames = prefs.getMacroNames();

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
}
