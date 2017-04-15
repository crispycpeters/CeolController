package com.candkpeters.ceol.widget;

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

}
