package com.candkpeters.ceol.cling;

import android.content.Context;
import android.util.Log;

import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.CeolDeviceOpenHome;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

import java.util.Map;

/**
 * Created by crisp on 14/04/2017.
 */

public class OpenHomeDevice {
    private static final String TAG = "OpenHomeDevice";

    private static final int DEFAULT_EVENT_RENEWAL_SECS = 600;
    private final Context context;
    private final CeolDevice ceolDevice;
    private final CeolDeviceOpenHome ceolDeviceOpenHome;
    private Device device;
    private AndroidUpnpService upnpService;
    private ServiceId infoServiceId = new ServiceId("av-openhome-org","Info");
    private ServiceId timeServiceId = new ServiceId("av-openhome-org","Time");


    public OpenHomeDevice(Context context, CeolDevice ceolDevice) {
        this.context = context;
        this.ceolDevice = ceolDevice;
        ceolDeviceOpenHome = ceolDevice.OpenHome;
    }

    public void removeDevice() {
        device = null;

        ceolDeviceOpenHome.setDuration(0);
        ceolDeviceOpenHome.setTrackCount(0);
        ceolDeviceOpenHome.setSeconds(0);
        ceolDeviceOpenHome.setUri("");
        ceolDeviceOpenHome.setMetadata("");
    }

    public Device getDevice() {
        return device;
    }

    public void addDevice(AndroidUpnpService upnpService, Device device) {
        this.upnpService = upnpService;
        this.device = device;
        DeviceDetails dd = device.getDetails();
        Log.d(TAG, "Details: " + dd.toString());
        DeviceIdentity di = device.getIdentity();
        Log.d(TAG, "Identity: " + di.toString());

        setupTimeEvents();
        setupInfoEvents();
    }

    private void setupTimeEvents() {
        Service timeService;
        if ((timeService = device.findService(timeServiceId)) != null) {
            Log.d(TAG,"Time service discovered: " + timeService);

//            executeAction(upnpService, infoService);

            SubscriptionCallback callback = new OpenHomeSubscriptionCallback(timeService) {

                @Override
                public void eventReceived(GENASubscription sub) {

                    Log.d(TAG,"Event: " + sub.getCurrentSequence().getValue());

                    try {
                        Map<String, StateVariableValue> values = sub.getCurrentValues();

                        UnsignedIntegerFourBytes durationV = (UnsignedIntegerFourBytes)(values.get("Duration").getValue());
                        Log.d(TAG, "EVENT: GOT duration=" + durationV);
                        ceolDeviceOpenHome.setDuration((long) (durationV.getValue()));

                        UnsignedIntegerFourBytes secondsV = (UnsignedIntegerFourBytes)(values.get("Seconds").getValue());
                        Log.d(TAG, "EVENT: GOT seconds=" + secondsV);
                        ceolDeviceOpenHome.setSeconds((long) (secondsV.getValue()));

                    } catch ( Exception e ) {
                        Log.e( TAG, "Bad values from event: " + e);
                    }
                }

            };

            upnpService.getControlPoint().execute(callback);

        }
    }

    private void setupInfoEvents() {
        Service infoService;
        if ((infoService = device.findService(infoServiceId)) != null) {
            Log.d(TAG,"Info service discovered: " + infoService);

//            executeAction(upnpService, infoService);

            SubscriptionCallback callback = new OpenHomeSubscriptionCallback(infoService) {

                @Override
                public void eventReceived(GENASubscription sub) {

                    Log.d(TAG,"Event: " + sub.getCurrentSequence().getValue());

                    Map<String, StateVariableValue> values = sub.getCurrentValues();

                    UnsignedIntegerFourBytes trackCountV = (UnsignedIntegerFourBytes)(values.get("TrackCount").getValue());
                    Log.d(TAG, "EVENT: GOT trackCount=" + trackCountV);
                    ceolDeviceOpenHome.setTrackCount((long)(trackCountV.getValue()));

                    StateVariableValue uri = values.get("Uri");
                    Log.d(TAG, "EVENT: GOT uri="+uri);
                    ceolDeviceOpenHome.setUri((String)(uri.getValue()));

                    StateVariableValue metadata = values.get("Metadata");
                    Log.d(TAG, "EVENT: GOT metadata="+metadata);
                    ceolDeviceOpenHome.setMetadata((String)(metadata.getValue()));
                }

            };

            upnpService.getControlPoint().execute(callback);

        }
    }


    private void executeAction(AndroidUpnpService upnpService, final Service infoService) {
        ActionInvocation actionInvocation = new ActionInvocation(infoService.getAction("Details"));

        upnpService.getControlPoint().execute(new ActionCallback(actionInvocation) {

                                                  @Override
                                                  public void success(ActionInvocation invocation) {
                                                      assert invocation.getOutput().length == 0;
                                                      Log.d(TAG,"Successfully called action!");
                                                  }

                                                  @Override
                                                  public void failure(ActionInvocation invocation,
                                                                      UpnpResponse operation,
                                                                      String defaultMsg) {
                                                      System.err.println(defaultMsg);
                                                  }
                                              }
        );
    }

    public abstract class OpenHomeSubscriptionCallback extends SubscriptionCallback {

        protected OpenHomeSubscriptionCallback(Service service) {
            super(service, DEFAULT_EVENT_RENEWAL_SECS);
        }

        @Override
        public void established(GENASubscription sub) {
            Log.d(TAG, "Established: " + sub.getSubscriptionId());
        }

        @Override
        protected void failed(GENASubscription subscription,
                              UpnpResponse responseStatus,
                              Exception exception,
                              String defaultMsg) {
            Log.d(TAG,"Subscripiton failed: " + defaultMsg);
        }

        @Override
        public void ended(GENASubscription sub,
                          CancelReason reason,
                          UpnpResponse response) {
//                    assertNull(reason);
            Log.d(TAG,"Subscription ended: "+ reason);
        }

        @Override
        public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
            Log.d(TAG,"Missed events: " + numberOfMissedEvents);
        }

    };

}
