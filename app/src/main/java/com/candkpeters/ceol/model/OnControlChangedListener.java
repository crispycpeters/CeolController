package com.candkpeters.ceol.model;

import com.candkpeters.ceol.model.control.AudioControl;
import com.candkpeters.ceol.model.control.CeolNavigatorControl;
import com.candkpeters.ceol.model.control.ConnectionControl;
import com.candkpeters.ceol.model.control.ControlBase;
import com.candkpeters.ceol.model.control.InputControl;
import com.candkpeters.ceol.model.control.PlaylistControlBase;
import com.candkpeters.ceol.model.control.PowerControl;
import com.candkpeters.ceol.model.control.TrackControl;

/**
 * Created by crisp on 03/05/2017.
 */

public interface OnControlChangedListener {
    void onControlChanged(CeolModel ceolModel, ObservedControlType observedControlType, ControlBase controlBase);
}
