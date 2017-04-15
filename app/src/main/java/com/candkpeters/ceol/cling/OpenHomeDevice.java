package com.candkpeters.ceol.cling;

import android.content.Context;
import android.util.Log;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.ServiceId;

import java.util.Map;

/**
 * Created by crisp on 14/04/2017.
 */

public class OpenHomeDevice {
    private static final String TAG = "OpenHomeDevice";
    private final Context context;
    private Device device;
    private AndroidUpnpService upnpService;
    private ServiceId infoServiceId = new ServiceId("av-openhome-org","Info");
    private ServiceId timeServiceId = new ServiceId("av-openhome-org","Time");


    public OpenHomeDevice(Context context) {
        this.context = context;
    }

    public void removeDevice() {
        device = null;
    }

    public Device getDevice() {
        return device;
    }

    public void addDevice(AndroidUpnpService upnpService, Device device) {
        this.upnpService = upnpService;
        DeviceDetails dd = device.getDetails();
        Log.d(TAG, "Details: " + dd.toString());
        DeviceIdentity di = device.getIdentity();
        Log.d(TAG, "Identity: " + di.toString());

        Service timeService;
        if ((timeService = device.findService(timeServiceId)) != null) {

            System.out.println("Time service discovered: " + timeService);

//            executeAction(upnpService, infoService);

            SubscriptionCallback callback = new SubscriptionCallback(timeService, 100) {

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
                public void eventReceived(GENASubscription sub) {

                    Log.d(TAG,"Event: " + sub.getCurrentSequence().getValue());

                    Map<String, StateVariableValue> values = sub.getCurrentValues();
                    StateVariableValue trackCount = values.get("TrackCount");
                    Log.d(TAG, "EVENT: GOT trackCount="+trackCount);
                    StateVariableValue duration = values.get("Duration");
                    Log.d(TAG, "EVENT: GOT duration="+duration);
                    StateVariableValue seconds = values.get("Seconds");
                    Log.d(TAG, "EVENT: GOT seconds="+seconds);

                    //assertEquals(status.getDatatype().getClass(), BooleanDatatype.class);
                    //assertEquals(status.getDatatype().getBuiltin(), Datatype.Builtin.BOOLEAN);

                }

                @Override
                public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
                    System.out.println("Missed events: " + numberOfMissedEvents);
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

}
