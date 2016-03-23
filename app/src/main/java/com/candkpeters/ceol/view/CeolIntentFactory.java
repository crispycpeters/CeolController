package com.candkpeters.ceol.view;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.candkpeters.ceol.device.command.Command;

import java.util.List;

/**
 * Created by crisp on 18/03/2016.
 */
public class CeolIntentFactory {

    public static final String TAG = "CeolIntentFactory";

    private static String getClassNameFromIntent(Intent intent) {
        String theClass = null;
        if ( intent != null && intent.getAction().equals(CeolService.EXECUTE_COMMAND) ) {
            Uri uri = intent.getData();
            Log.d(TAG, "getClassNameFromIntent: data = " + uri);
            List<String> paths = uri.getPathSegments();
            if ( paths.size() >= 1 ) {
                theClass = paths.get(0);
            }
        } else {
            Log.e(TAG, "getClassNameFromIntent: Command not recognized");
        }
        return theClass;
    }

    public static String getClassParameterFromIntent(Intent intent) {
        String param = null;
        if ( intent != null && intent.getAction().equals(CeolService.EXECUTE_COMMAND) ) {
            Uri uri = intent.getData();
            List<String> paths = uri.getPathSegments();
            if ( paths.size() == 2 ) {
                param = paths.get(1);
            }
        }
        return param;
    }

    public static Intent getIntent(Command command) {
        Intent intent = new Intent();
        intent.setAction(CeolService.EXECUTE_COMMAND);
        String param = command.getParameterAsString();
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.authority("com.candkpeters.ceol")
                .appendPath(command.getClass().getSimpleName())
                .appendPath(param)
                .build();
        intent.setData(uri);
        return intent;
    }

    public static Command newInstance(Intent intent) {
        Command command = null;

        String className = getClassNameFromIntent(intent);
        Log.d(TAG, "newInstance: Have Intent command name: " + className);
        if ( className != null ) {
            String valueString = getClassParameterFromIntent(intent);
            command = Command.newInstance(className, valueString);
        } else {
            Log.e(TAG, "newInstance: EXECUTE_COMMAND_NAME is not present");
        }
        return command;
    }

}
