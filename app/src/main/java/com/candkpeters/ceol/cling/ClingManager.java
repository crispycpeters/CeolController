package com.candkpeters.ceol.cling;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.candkpeters.ceol.view.Prefs;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.android.FixedAndroidLogHandler;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by crisp on 10/04/2017.
 */

public class ClingManager {
    private static String TAG = "ClingManager";
//    private BrowserUpnpService browserUpnpService;
    private AndroidUpnpService upnpService;
    private BrowseRegistryListener registryListener = new BrowseRegistryListener();
    private final Context context;
    private Prefs prefs;

    private OpenHomeDevice openHomeDevice;

    private Device cachedOpenHomeDevice;

    private void setupCachedPlayer() {
    }

    public ClingManager(Context context) {
        this.context = context;
        openHomeDevice = new OpenHomeDevice(context);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;

            // Clear the list
//            listAdapter.clear();

            // Set up cached OpenHome player
            setupCachedPlayer();

            // Get ready for future device advertisements
            upnpService.getRegistry().addListener(registryListener);

            // Now add all devices to the list we already know about
            for (Device device : upnpService.getRegistry().getDevices()) {
                registryListener.deviceAdded(device);
            }

            // Search asynchronously for all devices, they will respond soon
            Log.d(TAG,"Initiating search...");
            upnpService.getControlPoint().search();

        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    public void bindToCling() {

        openHomeDevice.removeDevice();
// Fix the logging integration between java.util.logging and Android internal logging
        org.seamless.util.logging.LoggingUtil.resetRootHandler(
                new FixedAndroidLogHandler()
        );
        // Now you can enable logging as needed for various categories of Cling:
        Logger.getLogger("org.fourthline.cling").setLevel(Level.FINEST);

        prefs = new Prefs(context);

        // This will start the UPnP service if it wasn't already started
        context.bindService(
                new Intent(context, AndroidUpnpServiceImpl.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    public void unbindFromCling() {
        if (upnpService != null) {
            openHomeDevice.removeDevice();
            upnpService.getRegistry().removeListener(registryListener);
        }
        context.unbindService(serviceConnection);
    }

    protected class BrowseRegistryListener extends DefaultRegistryListener {

        /* Discovery performance optimization for very slow Android devices! */
        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
//            deviceAdded(device);
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
/*            runOnUiThread(new Runnable() {
                public void run() {

                    Toast.makeText(
                            BrowserActivity.this,
                            "Discovery failed of '" + device.getDisplayString() + "': "
                                    + (ex != null ? ex.toString() : "Couldn't retrieve device/service descriptors"),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
*/
            deviceRemoved(device);
        }
        /* End of optimization, you can remove the whole block if your Android handset is fast (>= 600 Mhz) */

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            deviceRemoved(device);
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            deviceAdded(device);
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            deviceRemoved(device);
        }

        public void deviceAdded(final Device device) {
            Log.d(TAG, "Got device: " + device.toString());
            String friendlyName = device.getDetails().getFriendlyName();
            Log.d(TAG, "FriendlyName = " + friendlyName);
            String prefOpenhomeNmae = prefs.getOpenhomeName();

            if ( friendlyName.compareToIgnoreCase(prefOpenhomeNmae) == 0) {
                Log.d(TAG, "deviceAdded: Aha - found: " + device.getDisplayString());

                openHomeDevice.addDevice(upnpService, device);

            }
/*            runOnUiThread(new Runnable() {
                public void run() {

                    DeviceDisplay d = new DeviceDisplay(device);
                    int position = listAdapter.getPosition(d);
                    if (position >= 0) {
                        // Device already in the list, re-set new value at same position
                        listAdapter.remove(d);
                        listAdapter.insert(d, position);
                    } else {
                        listAdapter.add(d);
                    }

                }
            });
*/
        }

        public void deviceRemoved(final Device device) {

            if ( device.equals(openHomeDevice.getDevice())) {
                openHomeDevice.removeDevice();
            }
/*
            runOnUiThread(new Runnable() {
                public void run() {

                    listAdapter.remove(new DeviceDisplay(device));

                }
            });
*/
        }
    }


}
