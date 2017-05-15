package com.candkpeters.ceol.model;

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
