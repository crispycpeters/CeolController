package com.candkpeters.ceol.model;


import com.candkpeters.ceol.device.OnCeolStatusChangedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crisp on 03/05/2017.
 */

public abstract class ControlBase  {
    private static final String TAG = "ControlBase";

    protected ControlBase() {

    }


    protected abstract boolean copyFrom(ControlBase newControl);
}
