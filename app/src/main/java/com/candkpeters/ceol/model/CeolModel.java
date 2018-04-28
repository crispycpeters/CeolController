package com.candkpeters.ceol.model;

import android.util.Log;

import com.candkpeters.ceol.model.control.AudioControl;
import com.candkpeters.ceol.model.control.ConnectionControl;
import com.candkpeters.ceol.model.control.ControlBase;
import com.candkpeters.ceol.model.control.InputControl;
import com.candkpeters.ceol.model.control.PowerControl;
import com.candkpeters.ceol.model.control.ProgressControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crisp on 03/05/2017.
 */

public class CeolModel implements ControlObserved {
    final private static String TAG = "CeolModel";

    final public ConnectionControl connectionControl = new ConnectionControl();
    final public PowerControl powerControl = new PowerControl();
    final public AudioControl audioControl = new AudioControl();
    final public InputControl inputControl = new InputControl();
    final public ProgressControl progressControl = new ProgressControl();

    // ControlObserved
    private final Object MUTEX = new Object();
    private final List<OnControlChangedListener> observers;

    public long lastUpdateTimestamp;

    public CeolModel() {
        this.observers=new ArrayList<OnControlChangedListener>();
    }

    public void notifyConnectionStatus(boolean connection) {
        boolean hasChangedConnection = false;
        boolean newIsConnected = false;
        synchronized (MUTEX) {
            hasChangedConnection = connectionControl.updateConnected(connection);
            newIsConnected = connectionControl.isConnected();
        }
        if ( hasChangedConnection ) {
            if ( newIsConnected) {
                Log.d(TAG, "notifyConnectionStatus: We are connected. Refresh all observers." );
                notifyAllObservers();
            } else {
                Log.w(TAG, "notifyConnectionStatus: We are no longer connected." );
                notifyObservers(connectionControl);
            }
        }
    }

    // ControlObserved
    @Override
    public int register(OnControlChangedListener obj) {
        int observerSize;
        synchronized (MUTEX) {
            if ( obj != null ) {
                if (!observers.contains(obj)) observers.add(obj);
            }
            observerSize = observers.size();
        }
        refreshObserver(obj);
        return observerSize;
    }

    // ControlObserved
    @Override
    public int unregister(OnControlChangedListener obj) {
        int observerSize;
        synchronized (MUTEX) {
            if ( obj != null ) {
                observers.remove(obj);
            }
            observerSize = observers.size();
        }
        return observerSize;
    }

    // ControlObserved
    @Override
    public void notifyObservers( ControlBase control) {
        List<OnControlChangedListener> observersLocal = null;
        //synchronization is used to make sure any observer registered after message is received is not notified
        synchronized (MUTEX) {
            observersLocal = new ArrayList<>(this.observers);
        }
        for (OnControlChangedListener obj : observersLocal) {
            notifyObserver(obj, control);
        }
    }

    private void notifyObserver(OnControlChangedListener obj, ControlBase control) {
        if ( control != null) {
            obj.onControlChanged(this, control.getObservedControlType(), control);
        } else {
            obj.onControlChanged(this, ObservedControlType.All, null);
        }
    }

    public void notifyAllObservers() {
        notifyObservers(connectionControl);
        notifyObservers(powerControl);
        notifyObservers(audioControl);
        notifyObservers(inputControl);
        notifyObservers(inputControl.trackControl);
        notifyObservers(inputControl.navigatorControl);
        notifyObservers(inputControl.playlistControl);
        notifyObservers(progressControl);
    }

    public void refreshObserver( OnControlChangedListener obj) {
        notifyObserver(obj, connectionControl);
        notifyObserver(obj, powerControl);
        notifyObserver(obj, audioControl);
        notifyObserver(obj, inputControl);
        notifyObserver(obj, inputControl.trackControl);
        notifyObserver(obj, inputControl.navigatorControl);
        notifyObserver(obj, inputControl.playlistControl);
        notifyObserver(obj, progressControl);
    }


    public int registerCount() {
        List<OnControlChangedListener> observersLocal = null;
        //synchronization is used to make sure any observer registered after message is received is not notified
        synchronized (MUTEX) {
            observersLocal = new ArrayList<>(this.observers);
        }
        return observersLocal.size();
    }
}
