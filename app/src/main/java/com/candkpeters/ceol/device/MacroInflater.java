package com.candkpeters.ceol.device;

import android.util.Log;

import com.candkpeters.ceol.device.command.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crisp on 12/03/2016.
 */
public class MacroInflater {

    public final static int MACRO_COUNT = 8;
    private static final String TAG = "MacroInflater";
    public ArrayList<String> macroNames;
    public ArrayList<ArrayList<Command>> macroValues;

    public MacroInflater( String[]macroStringNames, String[]macroStringValues ) {

        this.macroNames = new ArrayList<String>(MACRO_COUNT);
        this.macroValues = new ArrayList<ArrayList<Command>>(MACRO_COUNT);
        for (String name :
                macroStringNames) {
            macroNames.add(name);
        }
        for (String value :
                macroStringValues) {
            macroValues.add(parseMacroCommands(value));
        }
    }

    private ArrayList<Command> parseMacroCommands(String value) {
        ArrayList<Command> commands = new ArrayList<Command>();

        if ( value != null ) {
            String[] lines = value.split("\\r?\\n");
            for (String line : lines) {
                Command command = parseLine(line.trim());
                if ( command == null) {
                    Log.e(TAG, "parseMacroCommands: Could not create " + line);
                } else {
                    commands.add(command);
                }
            }
        }
        return commands;
    }

    private Command parseLine(String line) {
        int sep = line.indexOf(":");
        String command;
        String parameter;
        if ( sep == 0 ) {
            return null;
        }
        if ( sep != -1 ) {
            command = line.substring(0,sep);
            parameter = line.substring(sep+1).trim();
        } else {
            command = line;
            parameter = null;
        }
        return createCommand(command, parameter);
    }

    private Command createCommand(String commandString, String parameterString) {
        if ( commandString == null || commandString.length()==0 )
            return null;
        if ( !commandString.startsWith("Command"))
            commandString = "Command" + commandString;
        return Command.newInstance(commandString, parameterString);
    }

    public ArrayList<Command> getMacro(int macroNumber) {
        if ( macroNumber <= 0 || macroNumber > MACRO_COUNT) return null;
        return macroValues.get(macroNumber-1);
    }
}
