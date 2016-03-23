package com.candkpeters.ceol.view;

import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import com.candkpeters.ceol.device.CeolCommandManager;

/**
 * Created by crisp on 22/03/2016.
 */
public class CeolWidgetHelperPlayer extends CeolWidgetHelper {

    @Override
    protected RemoteViews buildRemoteView(Context context, int widgetId) {
        return null;
    }

    @Override
    protected void updateViewsFirstTime(Context context, RemoteViews views, String text) {

    }

    @Override
    protected void updateViews(RemoteViews views, CeolCommandManager ceolCommandManager, Context context, String text) {

    }

    @Override
    public ComponentName getComponentName(Context context) {
        return new ComponentName(context, CeolWidgetProviderNavigator.class);
    }

}
