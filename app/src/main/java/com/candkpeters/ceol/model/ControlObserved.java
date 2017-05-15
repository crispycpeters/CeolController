package com.candkpeters.ceol.model;


/**
 * Created by crisp on 03/05/2017.
 */

public interface ControlObserved {
    //methods to register and unregister observers
    // Return number of registered listeners
    int register(OnControlChangedListener obj);
    // Return number of registered listeners
    int unregister(OnControlChangedListener obj);

    //method to notify observers of change
    void notifyObservers(ControlBase control);
}
