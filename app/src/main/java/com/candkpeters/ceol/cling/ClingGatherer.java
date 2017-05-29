package com.candkpeters.ceol.cling;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.candkpeters.ceol.device.GathererBase;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.SIStatusType;
import com.candkpeters.ceol.view.Prefs;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.android.FixedAndroidLogHandler;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by crisp on 10/04/2017.
 */

public class ClingGatherer extends GathererBase {
    private static String TAG = "ClingGatherer";
    //    private BrowserUpnpService browserUpnpService;
    private AndroidUpnpService upnpService;
    private BrowseRegistryListener registryListener = new BrowseRegistryListener();
    private final Context context;
    private Prefs prefs;
    private boolean isClingServiceBound;

    final private OpenHomeUpnpDevice openHomeUpnpDevice;
    private final CeolModel ceolModel;

    public OpenHomeUpnpDevice getOpenHomeUpnpDevice() {
        return openHomeUpnpDevice;
    }

    private Device cachedOpenHomeDevice;

    private void setupCachedPlayer() {
    }

    public ClingGatherer(Context context, CeolModel ceolModel) {
        this.context = context;
        openHomeUpnpDevice = new OpenHomeUpnpDevice(context, ceolModel);
        this.ceolModel = ceolModel;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            isClingServiceBound = true;
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
            checkSubscriptions();

        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
            isClingServiceBound = false;
        }
    };

    private void checkSubscriptions() {
        if ( isClingServiceBound) {
            if ( !openHomeUpnpDevice.isSubscribed() ) {
                Log.d(TAG, "Initiating search...");
                upnpService.getControlPoint().search();
            }
        }
    }

    public void bindToCling() {

        // This will start the UPnP service if it wasn't already started
        if ( !isClingServiceBound) {
            // Fix the logging integration between java.util.logging and Android internal logging
            org.seamless.util.logging.LoggingUtil.resetRootHandler(
                    new FixedAndroidLogHandler()
            );
            // Now you can enable logging as needed for various categories of Cling:
            Logger.getLogger("org.fourthline.cling").setLevel(Level.INFO);

            prefs = new Prefs(context);

            context.bindService(
                    new Intent(context, AndroidUpnpServiceImpl.class),
                    serviceConnection,
                    Context.BIND_AUTO_CREATE
            );
        }
    }

    public void unbindFromCling() {
        if (upnpService != null) {
            openHomeUpnpDevice.removeDevice();
            upnpService.getRegistry().removeListener(registryListener);
        }
        if ( isClingServiceBound) {
            context.unbindService(serviceConnection);
        }
    }

    @Override
    public void start(Prefs prefs) {
        if ( !isClingServiceBound) {
            bindToCling();
        } else {
            checkSubscriptions();
        }
    }

    @Override
    public void stop() {
        unbindFromCling();
    }

    public void checkOperation() {
        getOpenHomeUpnpDevice().checkOperation();
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
//            Log.d(TAG, "Got device: " + device.toString());
            String friendlyName = device.getDetails().getFriendlyName();
//            Log.d(TAG, "FriendlyName = " + friendlyName);
            String prefOpenhomeNmae = prefs.getOpenhomeName();

            if ( friendlyName.compareToIgnoreCase(prefOpenhomeNmae) == 0) {
                Log.d(TAG, "deviceAdded: Aha - found: " + device.getDisplayString());
                openHomeUpnpDevice.addDevice(upnpService, device);
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

            if ( device.equals(openHomeUpnpDevice.getDevice())) {
                Log.d(TAG, "deviceRemoved: " + device);
                openHomeUpnpDevice.removeDevice();

                // Need to switch back to ceol
                ceolModel.inputControl.updateSIStatus(SIStatusType.Unknown);
                ceolModel.notifyObservers(ceolModel.inputControl);
            }

        }
    }


}
