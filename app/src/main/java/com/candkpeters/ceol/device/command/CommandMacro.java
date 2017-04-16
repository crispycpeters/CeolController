package com.candkpeters.ceol.device.command;

import android.util.Log;

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

    public static ArrayList<Command> parseCommands(String value) {
        return null;
    }

    @Override
    protected void onCeolStatusChangedListener() {
        checkProgress();
        return;
    }

    private void checkProgress() {
        if ( commandSize > 0 ) {
            Command currentCommand = commands.get(commandPosition);
            Log.d(TAG, "checkProgress: checking " + currentCommand);
            if (currentCommand.isDone()) {
                if (currentCommand.isSuccessful()) {
                    Log.d(TAG, "checkProgress: Success, move to next");
                    commandPosition++;
                    if (commandPosition < commandSize) {
                        Command command = commands.get(commandPosition);
                        executeIfNeeded(command);
                    } else {
                        // We're done
                        setIsDone(true);
                    }
                } else {
                    // Stop remaining commands
                    Log.e(TAG, "checkProgress: Stopping macro as command " + currentCommand + " is done but was unsuccessful");
                    setIsDone(true);
                }
            }
        }
    }

    public void addCommand( Command command) {
        commands.add(command);
    }

    @Override
    protected boolean isSuccessful() {
        // TODO
        return commandSize == 0;
    }

    @Override
    protected void preExecute() {
        super.preExecute();

        maxExecutionTimeMsecs = 60000;
        commands = ceolManager.getMacro(getValue());
        commandSize = commands.size();
    }

    private void executeIfNeeded( Command command) {
        Log.d(TAG, "EXECUTE " + command);
        if ( !command.isSuccessful(ceolManager)) {
            command.execute(ceolManager);
        }
    }

    @Override
    public void execute() {
        commandPosition = 0;

        if ( commandSize > 0 ) {
            Command command = commands.get(commandPosition);
            executeIfNeeded(command);
        }
    }

}
