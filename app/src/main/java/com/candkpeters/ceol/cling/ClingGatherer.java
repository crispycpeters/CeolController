package com.candkpeters.ceol.cling;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
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

import static org.fourthline.cling.binding.xml.Descriptor.Device.ELEMENT.device;

/**
 * Created by crisp on 10/04/2017.
 */

public class ClingGatherer extends GathererBase implements Runnable {
    private static final long SEARCH_RETRY_MSECS = 15000;
    private static String TAG = "ClingGatherer";
    //    private BrowserUpnpService browserUpnpService;
    private AndroidUpnpService upnpService;
    private BrowseRegistryListener registryListener = new BrowseRegistryListener();
    private final Context context;
    private Prefs prefs;
    private boolean isClingServiceBound = false;
    private final OnClingListener onClingListener;
    private OpenHomeSubscriptionManager openHomeSubscriptionManager;
    private final CeolModel ceolModel;
    private boolean isPaused = false;
    private boolean isRestart;
    private Device device;

    public OpenHomeSubscriptionManager getOpenHomeSubscriptionManager() {
        return openHomeSubscriptionManager;
    }

    private Device cachedOpenHomeDevice;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "onServiceConnected: Cling service connected");
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
            Log.d(TAG, "onServiceConnected: Cling service disconnected");
            upnpService = null;
            isClingServiceBound = false;
            if ( isRestart) {
                isRestart = false;
                bindToCling();
            }
        }
    };

    private void setupCachedPlayer() {
    }

    public ClingGatherer(Context context, CeolModel ceolModel, OnClingListener onClingListener) {
        this.context = context;
        this.ceolModel = ceolModel;
        this.onClingListener = onClingListener;
    }

    @Override
    public void start(Prefs prefs) {
        isPaused = false;
        if ( !isClingServiceBound) {
            bindToCling();
        } else {
            checkSubscriptions();
        }
    }

    public void bindToCling() {

        // This will start the UPnP service if it wasn't already started
        if ( !isClingServiceBound) {
            // Fix the logging integration between java.util.logging and Android internal logging
            openHomeSubscriptionManager = new OpenHomeSubscriptionManager(context, ceolModel, new OnSubscriptionListener() {

                @Override
                public void onSubscriptionDisconnected() {

                }

                @Override
                public void onDeviceDisconnected() {
                    // Subscriptions have ended for some reason
                    Log.d(TAG, "onClingDisconnected: Subscriptions have ended");
                    deviceGone();
                }
            });

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

    private void checkSubscriptions() {
        if ( isClingServiceBound && !isPaused) {
            if ( !openHomeSubscriptionManager.hasDevice()) {
                Log.d(TAG, "Initiating search...");
                upnpService.getControlPoint().search(10);
                new Handler(context.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkSubscriptions();
                    }
                }, SEARCH_RETRY_MSECS);
            } else if ( !openHomeSubscriptionManager.isSubscribed()) {
                Log.d(TAG, "Setting up subscriptions...");
                openHomeSubscriptionManager.subscribe();
                new Handler(context.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkSubscriptions();
                    }
                }, SEARCH_RETRY_MSECS);
            }
        }
    }

    public void unbindFromCling() {
        if (upnpService != null) {
            Log.d(TAG, "unbindFromCling: Removing device");
            openHomeSubscriptionManager.removeDevice();
            //upnpService.getRegistry().removeListener(registryListener);
        }
        if ( isClingServiceBound) {
            try {
                Log.d(TAG, "unbindFromCling: Unbind service");
                context.unbindService(serviceConnection);
            } catch ( Exception exc ) {
                Log.w(TAG, "unbindFromCling: unbindService() failed, assuming unbound.",exc );
            }
        }
    }

    @Override
    public void stop() {
        unbindFromCling();
    }

    public void pause() {
        isPaused = true;
    }

    @Override
    public void run() {

    }

    private boolean isOpenHomeRunning() {
        return ( isClingServiceBound && openHomeSubscriptionManager.isSubscribed());
    }

    public void sendOpenHomeCommand(String commandString) {
        if ( isOpenHomeRunning()) {
            openHomeSubscriptionManager.performPlaylistCommand(commandString);
        }
    }

    public void sendOpenHomeSeekIdCommand(int trackId) {
        if ( isOpenHomeRunning()) {
            openHomeSubscriptionManager.performPlaylistSeekIdCommand(trackId);
        }
    }

    private void deviceGone() {
        openHomeSubscriptionManager.removeDevice();

        // Need to switch back to ceol
        notifyListeners();

        ceolModel.inputControl.updateSIStatus(SIStatusType.Unknown);
        ceolModel.notifyObservers(ceolModel.inputControl);
    }

    private void notifyListeners() {
        if ( onClingListener != null ) {
            onClingListener.onClingDisconnected();
        }
    }

    public void restart(Prefs prefs) {
        isRestart = true;
        stop();
        start(prefs);
    }

    private class BrowseRegistryListener extends DefaultRegistryListener {

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

                openHomeSubscriptionManager.addDevice(upnpService, device);
            }
        }

        public void deviceRemoved(final Device device) {

            if ( device.equals(openHomeSubscriptionManager.getDevice())) {
                Log.d(TAG, "deviceRemoved: " + device);
                deviceGone();
            }

        }

    }

}
