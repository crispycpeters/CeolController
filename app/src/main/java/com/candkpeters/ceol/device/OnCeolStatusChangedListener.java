package com.candkpeters.ceol.device;

import com.candkpeters.ceol.model.CeolDevice;

/**
 * Created by crisp on 20/02/2016.
 */
public interface OnCeolStatusChangedListener {
    public abstract void onCeolStatusChanged(CeolDevice ceolDevice );
}
