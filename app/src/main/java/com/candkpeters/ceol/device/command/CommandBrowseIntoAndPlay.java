package com.candkpeters.ceol.device.command;

import com.candkpeters.ceol.model.DirectionType;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandBrowseIntoAndPlay extends CommandBrowseInto {

    public CommandBrowseIntoAndPlay() {
        this(null);
    }

    public CommandBrowseIntoAndPlay( String value ) {
        super(value,true);
    }

}
