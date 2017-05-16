package com.candkpeters.ceol.model.control;


/**
 * Created by crisp on 03/05/2017.
 */

public abstract class ControlBase  {
    private static final String TAG = "ControlBase";

    protected ControlBase() {

    }


    protected abstract boolean copyFrom(ControlBase newControl);
}
