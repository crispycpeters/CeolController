package com.candkpeters.ceol.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.candkpeters.ceol.widget.CeolWidgetHelperMiniPlayer;
import com.candkpeters.ceol.widget.CeolWidgetProvider;

/**
 * Created by crisp on 12/02/2016.
 */
public class CeolWidgetProviderMiniPlayer extends CeolWidgetProvider {

    private static final String TAG = "WidgetMiniPlayer";

    public CeolWidgetProviderMiniPlayer() {
        super(new CeolWidgetHelperMiniPlayer());
    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        super.onReceive(context, intent);
//    }

}
