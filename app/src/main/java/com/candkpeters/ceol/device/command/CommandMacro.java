package com.candkpeters.ceol.device.command;

import android.content.Intent;
import android.util.Log;

import com.candkpeters.ceol.model.PlayStatusType;
import com.candkpeters.ceol.model.SIStatusType;

import java.util.ArrayList;

/**
 * Created by crisp on 25/01/2016.
 */
public class CommandMacro extends CommandBaseInteger {

    private static final String TAG = "CommandMacro";
    private ArrayList<Command> commands;
    int commandPosition = 0;
    int commandSize = 0;

    public CommandMacro() {
        this(-1);
    }

    public CommandMacro(int value) {
        super(value);
    }
//    CeolCommandManager.OnCeolStatusChangedListener onCeolStatusChangedListener;

    public static ArrayList<Command> parseCommands(String value) {
        return null;
    }


/*        onCeolStatusChangedListener = new CeolCommandManager.OnCeolStatusChangedListener() {
            @Override
            public void onCeolStatusChanged(CeolDevice ceolDevice) {
                checkProgress(ceolDevice);
            }
        };
*/
//        this.commands = new ArrayList<Command>();
/*
        addCommand(new CommandSetPower(true));
        addCommand(new CommandControl(PlayStatusType.Stop));
        addCommand(new CommandSetSI(SIStatusType.NetServer));
        addCommand(new CommandBrowseToRoot());
        addCommand(new CommandBrowseInto("naspeters2 FLAC",false));
        addCommand(new CommandBrowseInto("7 Playlists",false));
        addCommand(new CommandBrowseInto("Random pop 3",true));
*/

//        commandSize = commands.size();

        //ceolCommandManager.register(onCeolStatusChangedListener);

    @Override
    protected void onCeolStatusChangedListener() {
        checkProgress();
        return;
    };

    private void checkProgress() {
        Command currentCommand = commands.get(commandPosition);
        Log.d(TAG, "checkProgress: checking " + currentCommand);
        if ( currentCommand.isDone() ) {
            if ( currentCommand.isSuccessful()) {
                Log.d(TAG, "checkProgress: Success, move to next");
                commandPosition++;
                if (commandPosition < commandSize) {
                    commands.get(commandPosition).execute(ceolCommandManager);
                } else {
                    // We're done
                    setIsDone(true);
                }
            } else {
                // Stop remaining commands
                Log.e(TAG, "checkProgress: Stopping macro as command + " + currentCommand + " was unsuccessful");
                setIsDone(true);
            }
        }
    }

    public void addCommand( Command command) {
        commands.add(command);
    }

    @Override
    protected boolean isSuccessful() {
        return false; // TODO
    }

    @Override
    protected void preExecute() {
        super.preExecute();

        maxExecutionTimeMsecs = 60000;
        commands = ceolCommandManager.getMacro(getValue());
        commandSize = commands.size();
    }

    @Override
    public void execute() {
        commandPosition = 0;

        if ( commandSize > 0 ) {
            commands.get(commandPosition).execute(ceolCommandManager);
        }
    }

}
