package com.candkpeters.ceol.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crisp on 03/05/2017.
 */

public class CeolModel implements ControlObserved {
    final private static String TAG = "CeolModel";
//    final private static int NUM_CONTROLS = 6;
//    final private static int CONNECTION_CONTROL = 0;
//    final private static int POWER_CONTROL = 1;
//    final private static int INPUT_CONTROL = 2;
//    final private static int AUDIO_CONTROL = 3;
//    final private static int TRACK_CONTROL = 4;
//    final private static int CEOLNAVIGATOR_CONTROL = 5;

//    final private ControlBase[] controls = new ControlBase[NUM_CONTROLS];

    final public ConnectionControl connectionControl = new ConnectionControl();
    final public PowerControl powerControl = new PowerControl();
    final public AudioControl audioControl = new AudioControl();

    final public InputControl inputControl = new InputControl();


    // ControlObserved
    private final Object MUTEX = new Object();
    private final List<OnControlChangedListener> observers;

    public CeolModel() {
        this.observers=new ArrayList<OnControlChangedListener>();
    }


    public void notifyConnectionStatus(boolean connection) {
        boolean hasChangedConnection = false;
        synchronized (MUTEX) {
            hasChangedConnection = connectionControl.updateConnected(connection);
        }
        if ( hasChangedConnection ) {
            Log.d(TAG, "notifyConnectionStatus: Connection status has changed: " + connectionControl.isConnected());
            notifyObservers(connectionControl);
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
        if ( control instanceof ConnectionControl) {
            obj.onConnectionControlChanged(this, (ConnectionControl)control );
        } else if ( control instanceof InputControl) {
            obj.onInputControlChanged(this, (InputControl)control );
        } else if ( control instanceof TrackControl) {
            obj.onTrackControlChanged(this, (TrackControl) control );
        } else if ( control instanceof AudioControl) {
            obj.onCAudioControlChanged(this, (AudioControl) control );
        } else if ( control instanceof PowerControl) {
            obj.onPowerControlChanged(this, (PowerControl) control );
        } else if ( control instanceof CeolNavigatorControl) {
            obj.onCeolNavigatorControlChanged(this, (CeolNavigatorControl) control );
        }
    }

    public void refreshObserver( OnControlChangedListener obj) {
        notifyObserver(obj, connectionControl);
        notifyObserver(obj, powerControl);
        notifyObserver(obj, audioControl);
        notifyObserver(obj, inputControl);
        notifyObserver(obj, inputControl.trackControl);
        notifyObserver(obj, inputControl.navigatorControl);
    }


}
