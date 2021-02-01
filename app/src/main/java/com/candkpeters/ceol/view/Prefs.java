package com.candkpeters.ceol.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.candkpeters.chris.ceol.R;

import java.util.Map;

/**
 * Created by crisp on 05/01/2016.
 */
public class Prefs {

    private Context context = null;
    public static final int MACRO_COUNT = 8;
//    private static final String PREF_IS_DEBUG_MODE = "com.candkpeters.ceol.Is_Debug_Mode";
//    private static final String PREF_DEBUG_DEVICE_SERVER = "com.candkpeters.ceol.Debug_Device_Server";
//    private static final String PREF_USE_TELNET = "com.candkpeters.ceol.Use_Telnet";
//    private static final String PREF_USE_WEBSERVICE = "com.candkpeters.ceol.Use_Webservice";
//    private static final String PREF_WEB_STATUS_REPEAT_RATE_MSECS = "com.candkpeters.ceol.Web_Status_Repeat_Rate_Msecs";
//    private static final String PREF_END_MSECS = "com.candkpeters.ceol.End_Msecs";

    final SharedPreferences preferences;

    public Prefs(Context contextToUse)  {
        context = contextToUse;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        preferences = context.getSharedPreferences(
//                "com.candkpeters.ceol", Context.MODE_PRIVATE);
    }

    public void registerOnSharedPreferenceChangeListener( SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        preferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

/*    public String getBaseUrl() {
        return preferences.getString(PREF_URL,
                context.getResources().getStringArray(R.array.urls_array)[0] );
    }

    public void setBaseUrl( String url ) {
        preferences.edit().putString(PREF_URL, url).commit();
    }

    public String getFilename() {
        return preferences.getString(PREF_FILENAME,
                context.getResources().getStringArray(R.array.filenames_array)[0] );
    }

    public void setFilename( String filename ) {
        preferences.edit().putString(PREF_FILENAME, filename).commit();
    }
*/

    public String getDebugDeviceServer() {
        return preferences.getString(context.getResources().getString(R.string.pref_key_debug_device_server),
                context.getResources().getString(R.string.pref_default_debug_device_server));
    }

    public String getDefaultDeviceServer() {
        return preferences.getString(context.getResources().getString(R.string.pref_key_device_server),
                context.getResources().getString(R.string.pref_default_device_server));
    }

    public String getWssServer() {
        return preferences.getString(context.getResources().getString(R.string.pref_key_wss_server),
                context.getResources().getString(R.string.pref_default_wss_server));
    }

    public String[] getMacroNames() {
        String[] macroNames = new String[MACRO_COUNT];
        macroNames[0] = getMacro1Name();
        macroNames[1] = getMacro2Name();
        macroNames[2] = getMacro3Name();
        return macroNames;
    }

    public String[] getMacroValues() {
        String[] macroValues = new String[MACRO_COUNT];
        macroValues[0] = getMacro1Value();
        macroValues[1] = getMacro2Value();
        macroValues[2] = getMacro3Value();
        return macroValues;
    }

    public String getMacro1Name() {
        return preferences.getString(context.getResources().getString(R.string.pref_key_macro1name),
                context.getResources().getString(R.string.pref_default_macro1name));
    }

    public String getMacro1Value() {
        Map m = preferences.getAll();

        return preferences.getString(context.getResources().getString(R.string.pref_key_macro1),
                "");
    }

    public String getMacro2Name() {
        Map m = preferences.getAll();
        return preferences.getString(context.getResources().getString(R.string.pref_key_macro2name),
                context.getResources().getString(R.string.pref_default_macro2name));
    }

    public String getMacro2Value() {
        return preferences.getString(context.getResources().getString(R.string.pref_key_macro2),
                "");
    }

    public String getMacro3Name() {
        Map m = preferences.getAll();
        return preferences.getString(context.getResources().getString(R.string.pref_key_macro3name),
                context.getResources().getString(R.string.pref_default_macro2name));
    }

    public String getMacro3Value() {
        return preferences.getString(context.getResources().getString(R.string.pref_key_macro3),
                "");
    }

    public boolean getIsDebugMode() {
        return preferences.getBoolean(context.getResources().getString(R.string.pref_key_debug_mode),
                context.getResources().getBoolean(R.bool.pref_default_debug));
    }

    public boolean getIsOpenhomeEnabled() {
        return preferences.getBoolean(context.getResources().getString(R.string.pref_key_openhome_enable),
                context.getResources().getBoolean(R.bool.pref_default_openhome_enable));
    }

    public boolean getIsForegroundEnabled() {
        return preferences.getBoolean(context.getResources().getString(R.string.pref_key_foreground_enable),
                context.getResources().getBoolean(R.bool.pref_default_foreground_enable));
    }

    public int getBackgroundTimeoutSecs() {
        String key = context.getResources().getString(R.string.pref_key_backgroundtimeoutsecs);
        String def = context.getResources().getString(R.string.pref_default_backgroundtimeoutsecs);
        return Integer.valueOf(preferences.getString(key, def));
//        return preferences.getInt(context.getResources().getString(R.string.pref_key_backgroundtimeoutsecs),
//                context.getResources().getInteger(R.integer.pref_default_backgroundtimeoutsecs));
    }

    public int getBackgroundRateSecs() {
        String key = context.getResources().getString(R.string.pref_key_backgroundratesecs);
        String def = context.getResources().getString(R.string.pref_default_backgroundratesecs);
        return Integer.valueOf(preferences.getString(key, def));
    }

    public String getOpenhomeName() {
        if ( getIsDebugMode()) {
            return preferences.getString(context.getResources().getString(R.string.pref_key_debug_openhome_name),
                    context.getResources().getString(R.string.pref_default_debug_openhome_name));
        } else {
            return preferences.getString(context.getResources().getString(R.string.pref_key_openhome_name),
                    context.getResources().getString(R.string.pref_default_openhome_name));
        }
    }



/*
    public boolean getUseTelnet() {
        return preferences.getBoolean(PREF_USE_TELNET,
                context.getResources().getBoolean(R.bool.use_telnet) );
    }
*/

/*
    public boolean getUseWebservice() {
        return preferences.getBoolean(PREF_USE_WEBSERVICE,
                context.getResources().getBoolean(R.bool.use_webservice) );
    }
*/

    public String getBaseUrl() {
        if ( getIsDebugMode()) {
            return "http://"+getDebugDeviceServer()+"/";
        } else {
            return "http://"+getDefaultDeviceServer()+"/";
        }
    }


/*
    public int getWebStatusRepeatRateMsecs() {
        return preferences.getInt(PREF_WEB_STATUS_REPEAT_RATE_MSECS,
                context.getResources().getInteger(R.integer.web_status_repeat_rate_msecs) );
    }

    public int getEndMsecs() {
        return preferences.getInt(PREF_END_MSECS,
                context.getResources().getInteger(R.integer.end_rate_msecs) );
    }
*/
}
