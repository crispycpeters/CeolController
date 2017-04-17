package com.candkpeters.ceol.device;

import com.candkpeters.ceol.model.CeolDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crisp on 08/01/2016.
 */
public class CeolDeviceObserver implements Observed {

    private static final String TAG = "CeolDeviceObserver";

    // Observer
    private final Object MUTEX = new Object();
    private final List<OnCeolStatusChangedListener> observers;

    public CeolDeviceObserver() {
        this.observers=new ArrayList<OnCeolStatusChangedListener>();
    }

    @Override
    public int register(OnCeolStatusChangedListener obj) {
        int observerSize;
        synchronized (MUTEX) {
            if ( obj != null ) {
                if (!observers.contains(obj)) observers.add(obj);
            }
            observerSize = observers.size();
        }
        return observerSize;
    }

    @Override
    public int unregister(OnCeolStatusChangedListener obj) {
        int observerSize;
        synchronized (MUTEX) {
            if ( obj != null ) {
                observers.remove(obj);
            }
            observerSize = observers.size();
        }
        return observerSize;
    }

    @Override
    public void notifyObservers() {
        List<OnCeolStatusChangedListener> observersLocal = null;
        //synchronization is used to make sure any observer registered after message is received is not notified
        synchronized (MUTEX) {
            observersLocal = new ArrayList<>(this.observers);
        }
        for (OnCeolStatusChangedListener obj : observersLocal) {
            obj.onCeolStatusChanged();
        }
    }
}
