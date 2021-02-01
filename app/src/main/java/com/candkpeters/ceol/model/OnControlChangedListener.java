package com.candkpeters.ceol.model;

import com.candkpeters.ceol.model.control.ControlBase;

/**
 * Created by crisp on 03/05/2017.
 */

public interface OnControlChangedListener {
    void onControlChanged(CeolModel ceolModel, ObservedControlType observedControlType, ControlBase controlBase);
}
