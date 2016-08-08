package com.candkpeters.ceol.view;

import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.device.command.CommandApp;
import com.candkpeters.ceol.device.command.CommandControlStop;
import com.candkpeters.ceol.device.command.CommandControlToggle;
import com.candkpeters.ceol.device.command.CommandMasterVolumeDown;
import com.candkpeters.ceol.device.command.CommandMasterVolumeUp;
import com.candkpeters.ceol.device.command.CommandSetPowerToggle;
import com.candkpeters.ceol.device.command.CommandSkipBackward;
import com.candkpeters.ceol.device.command.CommandSkipForward;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.chris.ceol.R;

/**
 * Created by crisp on 22/03/2016.
 */
public class CeolWidgetHelperMiniPlayer extends CeolWidgetHelper {

    @Override
    public ComponentName getComponentName(Context context) {
        return new ComponentName(context, CeolWidgetProviderMiniPlayer.class);
    }

    @Override
    protected RemoteViews buildRemoteView(Context context, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout_miniplayer);
        setOnClickCommandIntent(context, appWidgetId, views, R.id.powerB, new CommandSetPowerToggle());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.volumeupB, new CommandMasterVolumeUp());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.volumedownB, new CommandMasterVolumeDown());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.skipBackwardsB, new CommandSkipBackward());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.skipForwardsB, new CommandSkipForward());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.playpauseB, new CommandControlToggle());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.stopB, new CommandControlStop());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.imageTrack, new CommandApp());
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

        views.setImageViewBitmap(R.id.imageTrack, ceolDevice.NetServer.getImageBitmap());
        views.setTextViewText(R.id.textTrack, ceolDevice.NetServer.getTrack());
        views.setTextViewText(R.id.textArtist, ceolDevice.NetServer.getArtist());
        views.setTextViewText(R.id.textAlbum, ceolDevice.NetServer.getAlbum());
    }

}
