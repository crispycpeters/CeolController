package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.PlayStatusType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandControlStop extends CommandControl {

    public CommandControlStop() {
        super(PlayStatusType.Stop);
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

}
