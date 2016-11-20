package com.candkpeters.ceol.device;

/**
 * Created by crisp on 28/01/2016.
 */
public interface Observed {
    //methods to register and unregister observers
    void register(OnCeolStatusChangedListener obj);
    void unregister(OnCeolStatusChangedListener obj);

    //method to notify observers of change
    void notifyObservers();

}
