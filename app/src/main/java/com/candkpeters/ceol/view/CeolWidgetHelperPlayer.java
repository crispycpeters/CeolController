package com.candkpeters.ceol.view;

import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import com.candkpeters.ceol.device.CeolCommandManager;
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
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.ceol.model.SIStatusType;
import com.candkpeters.chris.ceol.R;

/**
 * Created by crisp on 22/03/2016.
 */
public class CeolWidgetHelperPlayer extends CeolWidgetHelper {

    @Override
    protected RemoteViews buildRemoteView(Context context, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ceol_appwidget_layout_player);
        setOnClickIntent(context, appWidgetId, views, R.id.skipBackwardsB, new CommandSkipBackward());
        setOnClickIntent(context, appWidgetId, views, R.id.skipForwardsB, new CommandSkipForward());
        setOnClickIntent(context, appWidgetId, views, R.id.playpauseB, new CommandControl());
        setOnClickIntent(context, appWidgetId, views, R.id.stopB, new CommandControlStop());
        return views;
    }

    @Override
    protected void updateViewsFirstTime(Context context, RemoteViews views, String text) {
        views.setTextViewText(R.id.textUpdate, text);
    }

    String updateString = "";

    @Override
    protected void updateViews(RemoteViews views, CeolCommandManager ceolCommandManager, Context context, String text) {
        long curr = System.currentTimeMillis();
        CeolDevice ceolDevice = ceolCommandManager.getCeolDevice();

        views.setTextViewText(R.id.textUpdate, updateString + ": " + Long.toString(curr % 10000));

        views.setImageViewBitmap(R.id.imageTrack,ceolDevice.NetServer.getImageBitmap());
        views.setTextViewText(R.id.textTrack, ceolDevice.NetServer.getTrack());
        views.setTextViewText(R.id.textArtist, ceolDevice.NetServer.getArtist());
        views.setTextViewText(R.id.textAlbum, ceolDevice.NetServer.getAlbum());

    }

    @Override
    public ComponentName getComponentName(Context context) {
        return new ComponentName(context, CeolWidgetProviderPlayer.class);
    }

}
