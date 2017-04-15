package com.candkpeters.ceol.cling;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;

/**
 * Created by crisp on 12/04/2017.
 */
@UpnpService(
        serviceId = @UpnpServiceId("Info"),
        serviceType = @UpnpServiceType(value = "Info", version = 1)
)
public class OpenHomeInfo {

    @UpnpStateVariable(defaultValue = "", sendEvents = false)
    private int bitDepth;

    @UpnpStateVariable(defaultValue = "", sendEvents = false)
    private int bitRate;

    @UpnpStateVariable(defaultValue = "", sendEvents = false)
    private String codecName;

    @UpnpStateVariable(defaultValue = "", sendEvents = false)
    private int detailsCount;

    @UpnpStateVariable(defaultValue = "", sendEvents = false)
    private int duration;

    @UpnpStateVariable(defaultValue = "", sendEvents = false)
    private boolean lossless;

    @UpnpStateVariable(defaultValue = "", sendEvents = false)
    private String metadata;

    @UpnpStateVariable(defaultValue = "", sendEvents = false)
    private int metatextCount;

    @UpnpStateVariable(defaultValue = "", sendEvents = false)
    private int trackCount;

    @UpnpStateVariable(defaultValue = "", sendEvents = false)
    private String uri;

    @UpnpAction(name = "Counters")
    public int getCounters() {
        return trackCount;
    }

    @UpnpAction(name = "Details")
    public int getDetails() {
        return duration;
    }

    @UpnpAction(name = "Track")
    public String getTrack() {
        return uri;
    }
}
