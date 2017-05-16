package com.candkpeters.ceol.model;

import com.candkpeters.ceol.model.control.AudioControl;
import com.candkpeters.ceol.model.control.CeolNavigatorControl;
import com.candkpeters.ceol.model.control.ConnectionControl;
import com.candkpeters.ceol.model.control.InputControl;
import com.candkpeters.ceol.model.control.PowerControl;
import com.candkpeters.ceol.model.control.TrackControl;

/**
 * Created by crisp on 03/05/2017.
 */

public interface OnControlChangedListener {
    void onCAudioControlChanged(CeolModel ceolModel,AudioControl audioControl);
    void onConnectionControlChanged(CeolModel ceolModel,ConnectionControl connectionControl);
    void onCeolNavigatorControlChanged(CeolModel ceolModel,CeolNavigatorControl ceolNavigatorControl);
    void onInputControlChanged(CeolModel ceolModel,InputControl inputControl);
    void onPowerControlChanged(CeolModel ceolModel,PowerControl powerControl);
    void onTrackControlChanged(CeolModel ceolModel,TrackControl trackControl);
}
