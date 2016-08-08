package com.candkpeters.ceol.view;

/**
 * Created by crisp on 12/02/2016.
 */
public class CeolWidgetProviderMiniPlayer extends CeolWidgetProvider {

    private static final String TAG = "WidgetMiniPlayer";

    public CeolWidgetProviderMiniPlayer() {
        super(new CeolWidgetHelperMiniPlayer());
    }

}
