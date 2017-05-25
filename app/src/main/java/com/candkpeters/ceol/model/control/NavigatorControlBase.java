package com.candkpeters.ceol.model.control;

import com.candkpeters.ceol.model.ObservedControlType;

/**
 * Created by crisp on 03/05/2017.
 */

public class NavigatorControlBase extends ControlBase {

    private static final String TAG = "NavigatorControlBase";

    public NavigatorControlBase() {
        super(ObservedControlType.Navigator);
    }

    @Override
    protected boolean copyFrom(ControlBase newControl) {
        return false;
    }
}
