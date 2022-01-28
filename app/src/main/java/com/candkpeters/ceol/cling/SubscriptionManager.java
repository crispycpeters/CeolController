package com.candkpeters.ceol.cling;

import android.util.Log;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceId;

public abstract class SubscriptionManager {
    private static final String TAG = "SubscriptionManager";


    public abstract void offerDevice(Device device);

    protected Service findService(Device device, ServiceId serviceId) {
        Service service;
        if ((service = device.findService(serviceId)) == null) {
            Log.e(SubscriptionManager.TAG, "No service for " + serviceId);
        }
        return service;
    }
}
