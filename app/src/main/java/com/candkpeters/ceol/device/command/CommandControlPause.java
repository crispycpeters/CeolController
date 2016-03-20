package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.PlayStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandControlPause extends CommandControl {

    public CommandControlPause() {
        super(PlayStatusType.Pause);
    }
}
