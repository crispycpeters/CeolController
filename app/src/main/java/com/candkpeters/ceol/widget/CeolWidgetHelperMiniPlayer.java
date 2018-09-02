package com.candkpeters.ceol.widget;

import android.content.ComponentName;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import com.candkpeters.ceol.device.CeolManager;
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
import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolModel;
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
        setOnClickCommandIntent(context, appWidgetId, views, R.id.dimV, new CommandAppBasic());
        return views;
    }

    @Override
    protected void updateViewsFirstTime(Context context, RemoteViews views, String text) {
        views.setTextViewText(R.id.textUpdate, text);
    }

    String updateString = "";

    @Override
    protected void updateViews(RemoteViews views, CeolManager ceolManager, Context context, String text) {
        long curr = System.currentTimeMillis();
        CeolModel ceolModel= ceolManager.ceolModel;

        views.setTextViewText(R.id.textUpdate,  Long.toString(curr % 100));

        views.setTextViewText(R.id.playStatus, ceolModel.inputControl.trackControl.getPlayStatus().toString());
        views.setTextViewText(R.id.volume, ceolModel.audioControl.getMasterVolumeString());

        switch ( ceolModel.inputControl.getSIStatus()) {

            case Tuner:
                AudioStreamItem audioTunerItem = ceolModel.inputControl.trackControl.getAudioItem();
                views.setTextViewText(R.id.tunerBand, audioTunerItem.getBand());
                views.setTextViewText(R.id.tunerName, audioTunerItem.getTitle());
                views.setTextViewText(R.id.tunerFrequency, audioTunerItem.getFrequency());

                views.setViewVisibility(R.id.netPanel, View.INVISIBLE);
                views.setViewVisibility(R.id.tunerPanel, View.VISIBLE);
                views.setViewVisibility(R.id.dimV, View.INVISIBLE);
                break;
//            case Unknown:
//                views.setViewVisibility(R.id.netPanel, View.INVISIBLE);
//                views.setViewVisibility(R.id.tunerPanel, View.INVISIBLE);
//                views.setViewVisibility(R.id.dimV, View.VISIBLE);
//                break;
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
                AudioStreamItem audioStreamItem= ceolModel.inputControl.trackControl.getAudioItem();

                views.setImageViewBitmap(R.id.imageTrack, audioStreamItem.getImageBitmap());
                views.setTextViewText(R.id.textTrack, audioStreamItem.getTitle());
                views.setTextViewText(R.id.textArtist, audioStreamItem.getArtist());
                views.setTextViewText(R.id.textAlbum, audioStreamItem.getAlbum());

                views.setViewVisibility(R.id.netPanel, View.VISIBLE);
                views.setViewVisibility(R.id.tunerPanel, View.INVISIBLE);
                views.setViewVisibility(R.id.dimV, View.INVISIBLE);
                break;
        }


        if ( ceolModel.connectionControl.isConnected() ) {
            views.setViewVisibility(R.id.dimV, View.GONE);
            views.setTextViewText(R.id.siB, ceolModel.inputControl.getSIStatus().name);

            switch ( ceolModel.powerControl.getDeviceStatus()) {

                case Connecting:
                    break;
                case Standby:
                    views.setViewVisibility(R.id.waitingPB, View.GONE);
                    views.setViewVisibility(R.id.powerV, View.GONE);
                    views.setImageViewResource(R.id.powerB, R.drawable.ic_av_power_off);
//                    views.setImageViewResource(R.id.powerB2, R.drawable.ic_av_power_off);
//                    views.setInt(R.id.siB, "setBackgroundResource", R.color.dimmedWhite);
                    break;
                case Starting:
                    views.setViewVisibility(R.id.waitingPB, View.VISIBLE);
                    views.setImageViewResource(R.id.powerB, R.drawable.ic_av_power_off);
                    views.setViewVisibility(R.id.powerV, View.VISIBLE);
//                    views.setImageViewResource(R.id.powerB2, R.drawable.ic_av_power_off);
//                    views.setInt(R.id.siB, "setBackgroundResource", R.color.dimmedWhite);
                    break;
                case On:
                    views.setViewVisibility(R.id.waitingPB, View.GONE);
                    views.setViewVisibility(R.id.powerV, View.GONE);
                    views.setImageViewResource(R.id.powerB, R.drawable.ic_av_power);
//                    views.setInt(R.id.siB, "setBackgroundResource", R.color.white);
                    break;
            }


        } else {
            views.setViewVisibility(R.id.dimV, View.VISIBLE);
            views.setTextViewText(R.id.siB, context.getString(R.string.not_connected_short));

            views.setViewVisibility(R.id.waitingPB, View.GONE);
            views.setViewVisibility(R.id.powerV, View.GONE);
            views.setImageViewResource(R.id.powerB, R.drawable.ic_av_power_off);
        }

    }

    @Override
    protected void updateViewsOnDestroy(RemoteViews views, CeolManager ceolManager, Context context) {

        views.setViewVisibility(R.id.dimV, View.VISIBLE);

    }

}
