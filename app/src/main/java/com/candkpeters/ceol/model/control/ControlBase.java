package com.candkpeters.ceol.model.control;


import com.candkpeters.ceol.model.ObservedControlType;

/**
 * Created by crisp on 03/05/2017.
 */

public abstract class ControlBase  {
    private static final String TAG = "ControlBase";

    private ObservedControlType observedControlType = ObservedControlType.All;

    protected ControlBase( ObservedControlType observedControlType) {
        this.observedControlType = observedControlType;
    }

    public ObservedControlType getObservedControlType() {
        return observedControlType;
    }

    protected abstract boolean copyFrom(ControlBase newControl);
}
