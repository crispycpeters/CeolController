package com.candkpeters.ceol.device.command;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandBrowseIntoAndPlay extends CommandBrowseInto {

    public CommandBrowseIntoAndPlay() {
        this(null);
    }

    private CommandBrowseIntoAndPlay(String value) {
        super(value,true);
    }

}
