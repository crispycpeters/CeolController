package com.candkpeters.ceol.device;

import android.util.Log;

import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.DeviceStatusType;

/**
 * Created by crisp on 31/12/2015.
 */
public class CeolTelnetParser {
    private static final int ENTRYROWS = 9;
    private static final String TAG = "CeolTelnetParser";
    private static final char CR_CHAR = 0x0d;
    private String[] NseRows;

    public CeolTelnetParser() {
        NseRows = new String[ENTRYROWS];
    }

    public void setCeolStatus(String telnetEntry) {

        String[] rows = telnetEntry.split("\r");
        for (String row1 : rows) {
            String row = row1;
            if (row.startsWith("NSE")) {
                String rownumberstring = row.substring(3, 4);
                row = row.substring(4);
                int rownumber = -1;
                boolean isPlayable = false;
                boolean isSelected = false;
                int status = 0;
                try {
                    rownumber = Integer.parseInt(rownumberstring);
                } catch (NumberFormatException e) {
                    rownumber = -1;
                }
                if (rownumber >= 0 && rownumber <= 8) {
                    switch (rownumber) {
                        case 0:
                            NseRows[0] = row;
                            Log.d(TAG, "Parsed NSE0: " + NseRows[0]);
                            break;
                        default:
                            if (row.length() > 0) {
                                NseRows[rownumber] = row.substring(1);
                                status = row.charAt(0);
                                isPlayable = (status & 0x01) != 0;
                                isSelected = (status & 0x08) != 0;
                            } else {
                                NseRows[rownumber] = "";
                            }
                            Log.d(TAG, "Parsed NSE" + rownumber + ": " + NseRows[rownumber] + (isPlayable ? " [P]" : "") + (isSelected ? " [S]" : "") + status);
                            break;
                    }
                }
            } else if (row.startsWith("PW")) {
                boolean isOn = row.substring(2).equals("ON");
                CeolDevice.getInstance().setDeviceStatus(DeviceStatusType.On);
            } else if (row.startsWith("MU")) {
                CeolDevice.getInstance().setIsMuted(row.substring(2).equals("ON"));
            } else if (row.startsWith("MV")) {
                int volume = 0;
                try {
                    volume = Integer.parseInt(row.substring(2));
                    CeolDevice.getInstance().setMasterVolume(volume);
                } catch (NumberFormatException e) {
                    Log.d(TAG, "setCeolStatus: Bad volume number: " + row.substring(3));
                }
            } else {
                Log.d(TAG, "Unparsed CEOL event: " + row);
            }
        }
    }
}
