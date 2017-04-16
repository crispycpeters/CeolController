package com.candkpeters.ceol.model;

import org.fourthline.cling.model.state.StateVariableValue;

/**
 * Created by crisp on 16/04/2017.
 */

public class CeolDeviceOpenHome {

    private long trackCount;
    private long duration;
    private long seconds;
    private String uri;
    private String metadata;

    public boolean isOperating( ) {
        if (trackCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setTrackCount(long trackCount) {
        this.trackCount = trackCount;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
