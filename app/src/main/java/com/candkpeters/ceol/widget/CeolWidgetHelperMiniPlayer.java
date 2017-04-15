package com.candkpeters.ceol.widget;

import android.content.ComponentName;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.device.command.CommandAppBasic;
import com.candkpeters.ceol.device.command.CommandAppInfo;
import com.candkpeters.ceol.device.command.CommandAppSelectSI;
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
        setOnClickCommandIntent(context, appWidgetId, views, R.id.powerB2, new CommandSetPowerToggle());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.volumeupB, new CommandMasterVolumeUp());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.volumedownB, new CommandMasterVolumeDown());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.skipBackwardsB, new CommandSkipBackward());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.skipForwardsB, new CommandSkipForward());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.playpauseB, new CommandControlToggle());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.stopB, new CommandControlStop());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.album_art, new CommandAppBasic());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.siB, new CommandAppSelectSI());
        setOnClickCommandIntent(context, appWidgetId, views, R.id.infoB, new CommandAppInfo());
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

        views.setTextViewText(R.id.textUpdate,  Long.toString(curr % 100));

        views.setImageViewBitmap(R.id.imageTrack, ceolDevice.NetServer.getImageBitmap());
        views.setTextViewText(R.id.textTrack, ceolDevice.NetServer.getTrack());
        views.setTextViewText(R.id.textArtist, ceolDevice.NetServer.getArtist());
        views.setTextViewText(R.id.textAlbum, ceolDevice.NetServer.getAlbum());
        views.setTextViewText(R.id.playStatus, ceolDevice.getPlayStatus().toString());
        views.setTextViewText(R.id.volume, ceolDevice.getMasterVolumeString());
        views.setTextViewText(R.id.tunerBand, ceolDevice.Tuner.getBand());
        views.setTextViewText(R.id.tunerName, ceolDevice.Tuner.getName());
        views.setTextViewText(R.id.tunerFrequency, ceolDevice.Tuner.getFrequency());
        views.setTextViewText(R.id.siB, ceolDevice.getSIStatus().name);

        switch ( ceolDevice.getSIStatus()) {

            case Tuner:
                views.setViewVisibility(R.id.netPanel, View.INVISIBLE);
                views.setViewVisibility(R.id.tunerPanel, View.VISIBLE);
                views.setViewVisibility(R.id.dimV, View.INVISIBLE);
                break;
            case NotConnected:
                views.setViewVisibility(R.id.netPanel, View.INVISIBLE);
                views.setViewVisibility(R.id.tunerPanel, View.INVISIBLE);
                views.setViewVisibility(R.id.dimV, View.VISIBLE);
                break;
            case CD:
            case AnalogIn:
                views.setViewVisibility(R.id.netPanel, View.INVISIBLE);
                views.setViewVisibility(R.id.tunerPanel, View.INVISIBLE);
                views.setViewVisibility(R.id.dimV, View.INVISIBLE);
                break;
            default:
            case IRadio:
            case DigitalIn1:
            case DigitalIn2:
            case NetServer:
            case Bluetooth:
            case Ipod:
            case Spotify:
                views.setViewVisibility(R.id.netPanel, View.VISIBLE);
                views.setViewVisibility(R.id.tunerPanel, View.INVISIBLE);
                views.setViewVisibility(R.id.dimV, View.INVISIBLE);
                break;
        }

        switch ( ceolDevice.getDeviceStatus()) {

            case Connecting:
                views.setViewVisibility(R.id.waitingPB, View.GONE);
                break;
            case Standby:
                views.setViewVisibility(R.id.powerV, View.VISIBLE);
                views.setImageViewResource(R.id.powerB, R.drawable.ic_av_power_back);
                views.setImageViewResource(R.id.powerB2, R.drawable.ic_av_power_back);
                views.setViewVisibility(R.id.waitingPB, View.GONE);
                break;
            case Starting:
                views.setViewVisibility(R.id.powerV, View.VISIBLE);
                views.setImageViewResource(R.id.powerB2, R.drawable.ic_av_power_back);
                views.setViewVisibility(R.id.waitingPB, View.VISIBLE);
                break;
            case On:
                views.setViewVisibility(R.id.powerV, View.GONE);
                views.setImageViewResource(R.id.powerB, R.drawable.ic_av_power);
                break;
        }
    }

}
