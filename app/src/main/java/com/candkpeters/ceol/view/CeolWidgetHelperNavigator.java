package com.candkpeters.ceol.view;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.RemoteViews;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.device.command.CommandCursor;
import com.candkpeters.ceol.device.command.CommandCursorEnter;
import com.candkpeters.ceol.device.command.CommandMasterVolumeDown;
import com.candkpeters.ceol.device.command.CommandMasterVolumeUp;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.chris.ceol.R;

/**
 * Created by crisp on 22/03/2016.
 */
public class CeolWidgetHelperNavigator extends CeolWidgetHelper {

    @Override
    public ComponentName getComponentName(Context context) {
        return new ComponentName(context, CeolWidgetProviderNavigator.class);
    }

    @Override
    protected RemoteViews buildRemoteView(Context context, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout_navigator);
//        setOnClickIntent(context, appWidgetId, views, R.id.powerB, new CommandSetPowerToggle());
//        setOnClickIntent(context, appWidgetId, views, R.id.performMacro1B, new CommandMacro(1));
//        setOnClickIntent(context, appWidgetId, views, R.id.performMacro2B, new CommandMacro(2));
//        setOnClickIntent(context, appWidgetId, views, R.id.performMacro3B, new CommandMacro(3));
        setOnClickIntent(context, appWidgetId, views, R.id.volumeupB, new CommandMasterVolumeUp());
        setOnClickIntent(context, appWidgetId, views, R.id.volumedownB, new CommandMasterVolumeDown());
//        setOnClickIntent(context, appWidgetId, views, R.id.fastBackwardsB, new CommandFastBackward());
//        setOnClickIntent(context, appWidgetId, views, R.id.fastForwardsB, new CommandFastForward());
//        setOnClickIntent(context, appWidgetId, views, R.id.skipBackwardsB, new CommandSkipBackward());
//        setOnClickIntent(context, appWidgetId, views, R.id.skipForwardsB, new CommandSkipForward());
//        setOnClickIntent(context, appWidgetId, views, R.id.playpauseB, new CommandControlToggle());
//        setOnClickIntent(context, appWidgetId, views, R.id.stopB, new CommandControlStop());
        setOnClickIntent(context, appWidgetId, views, R.id.navLeftB, new CommandCursor(DirectionType.Left));
        setOnClickIntent(context, appWidgetId, views, R.id.navRightB, new CommandCursor(DirectionType.Right));
        setOnClickIntent(context, appWidgetId, views, R.id.navUpB, new CommandCursor(DirectionType.Up));
        setOnClickIntent(context, appWidgetId, views, R.id.navDownB, new CommandCursor(DirectionType.Down));
        setOnClickIntent(context, appWidgetId, views, R.id.navEnterB, new CommandCursorEnter());
//        setOnClickIntent(context, appWidgetId, views, R.id.siInternetRadioB, new CommandSetSI(SIStatusType.IRadio));
//        setOnClickIntent(context, appWidgetId, views, R.id.siIpodB, new CommandSetSI(SIStatusType.Ipod));
//        setOnClickIntent(context, appWidgetId, views, R.id.siMusicServerB, new CommandSetSI(SIStatusType.NetServer));
//        setOnClickIntent(context, appWidgetId, views, R.id.siTunerB, new CommandSetSI(SIStatusType.Tuner));
//        setOnClickIntent(context, appWidgetId, views, R.id.siAnalogInB, new CommandSetSI(SIStatusType.AnalogIn));
//        setOnClickIntent(context, appWidgetId, views, R.id.siDigitalInB, new CommandSetSI(SIStatusType.DigitalIn1));
//        setOnClickIntent(context, appWidgetId, views, R.id.siBluetoothB, new CommandSetSI(SIStatusType.Bluetooth));
//        setOnClickIntent(context, appWidgetId, views, R.id.siCdB, new CommandSetSI(SIStatusType.CD));

        return views;
    }

    @Override
    protected void updateViewsFirstTime(Context context, RemoteViews views, String text) {
//        updateMacroButtons(context, views);
        views.setTextViewText(R.id.textUpdate, text);
    }

    String updateString = "";

    @Override
    protected void updateViews(RemoteViews views, CeolCommandManager ceolCommandManager, Context context, String text) {
        long curr = System.currentTimeMillis();
        CeolDevice ceolDevice = ceolCommandManager.getCeolDevice();

        views.setViewVisibility(R.id.waitingPB, isWaiting?View.VISIBLE:View.INVISIBLE);
        views.setTextViewText(R.id.textUpdate, updateString + ": " + Long.toString(curr % 10000));

//        updateMacroButtons(context, views);

        setRowView(views, ceolDevice, R.id.textRow0, 0);
        setRowView(views, ceolDevice, R.id.textRow1, 1);
        setRowView(views, ceolDevice, R.id.textRow2, 2);
        setRowView(views, ceolDevice, R.id.textRow3, 3);
        setRowView(views, ceolDevice, R.id.textRow4, 4);
        setRowView(views, ceolDevice, R.id.textRow5, 5);
        setRowView(views, ceolDevice, R.id.textRow6, 6);
        setRowView(views, ceolDevice, R.id.textRow7, 7);

    }

    private void setRowView(RemoteViews views, CeolDevice ceolDevice, int viewId, int rowindex) {
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

/*
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
*/
}
