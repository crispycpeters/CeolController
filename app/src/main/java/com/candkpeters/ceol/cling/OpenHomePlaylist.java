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
        serviceId = @UpnpServiceId("Playlist"),
        serviceType = @UpnpServiceType(value = "Playlist", version = 1)
)
public class OpenHomePlaylist {

    @UpnpAction(name = "Play")
    public void play() {
        return ;
    }

    @UpnpAction(name = "Pause")
    public void pause() {
        return ;
    }

    @UpnpAction(name = "Stop")
    public void stop() {
        return ;
    }

    @UpnpAction(name = "Next")
    public void next() {
        return ;
    }

    @UpnpAction(name = "Previous")
    public void previous() {
        return ;
    }

}
