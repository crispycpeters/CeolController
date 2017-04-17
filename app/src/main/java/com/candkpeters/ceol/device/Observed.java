package com.candkpeters.ceol.device;

/**
 * Created by crisp on 28/01/2016.
 */
public interface Observed {
    //methods to register and unregister observers
    // Return number of registered listeners
    int register(OnCeolStatusChangedListener obj);
    // Return number of registered listeners
    int unregister(OnCeolStatusChangedListener obj);

    //method to notify observers of change
    void notifyObservers();

}
