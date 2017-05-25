package com.candkpeters.ceol.model.control;

import com.candkpeters.ceol.model.ObservedControlType;

/**p
 * Created by crisp on 03/05/2017.
 */

public class ConnectionControl extends ControlBase {

    private static final String TAG = "ConnectionControl";
    protected boolean isConnected = false;

    public ConnectionControl() {
        super(ObservedControlType.Connection);
    }

    @Override
    public boolean copyFrom(ControlBase newControl) {
        boolean hasChanged = false;
        if ( newControl != null && newControl instanceof ConnectionControl) {
            ConnectionControl newConnectionControl = (ConnectionControl)newControl;
            if (this.isConnected != newConnectionControl.isConnected) {
                this.isConnected = newConnectionControl.isConnected;
                hasChanged = true;
            }
        }
        return hasChanged;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean updateConnected( boolean isConnected) {
        if ( this.isConnected != isConnected ) {
            this.isConnected = isConnected;
            return true;
        } else {
            return false;
        }
    }
}
