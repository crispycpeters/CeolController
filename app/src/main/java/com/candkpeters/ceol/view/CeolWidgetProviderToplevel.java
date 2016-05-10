package com.candkpeters.ceol.view;

/**
 * Created by crisp on 12/02/2016.
 */
public class CeolWidgetProviderToplevel extends CeolWidgetProvider {

    private static final String TAG = "WidgetTopLevel";

    public CeolWidgetProviderToplevel() {
        super(new CeolWidgetHelperToplevel());
    }
}
