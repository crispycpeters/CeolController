package com.candkpeters.ceol.controller;

import android.content.Context;

import com.candkpeters.ceol.device.CeolCommandManager;
import com.candkpeters.ceol.device.CeolDeviceWebSvcMonitor;
import com.candkpeters.ceol.device.OnCeolStatusChangedListener;
import com.candkpeters.ceol.device.command.CommandMacro;
import com.candkpeters.ceol.device.MobileTelnetSdkReceiver;
import com.candkpeters.ceol.device.command.CommandMasterVolume;
import com.candkpeters.ceol.device.command.CommandSkip;
import com.candkpeters.ceol.model.CeolDevice;
import com.candkpeters.ceol.model.DirectionType;
import com.candkpeters.ceol.view.Prefs;

//import org.apache.commons.net.telnet.EchoOptionHandler;
//import org.apache.commons.net.telnet.SuppressGAOptionHandler;
//import org.apache.commons.net.telnet.TelnetClient;
//import org.apache.commons.net.telnet.TerminalTypeOptionHandler;


/**
 * Created by crisp on 07/01/2016.
 */
public class CeolController {
    private static final String TAG = "CeolController";

    static MobileTelnetSdkReceiver telnetReceiver = null;
    CeolDeviceWebSvcMonitor ceolWebService = null;
    Prefs prefs;
//    TelnetClient telnetClient = null;
    CeolDevice ceolDevice;
    CeolCommandManager ceolCommandManager;

/*
    public static interface OnCeolStatusChangedListener {
        public abstract void onCeolStatusChanged(CeolDevice ceolDevice );
    }
*/

    public CeolController( Context context, final OnCeolStatusChangedListener onCeolStatusChangedListener ) {
        this.prefs = new Prefs(context);
        String baseurl = prefs.getBaseUrl();

//        ceolWebService = new CeolDeviceWebSvcMonitor(baseurl);
        ceolDevice = CeolDevice.getInstance();
        ceolCommandManager = CeolCommandManager.getInstance();
        ceolCommandManager.setDevice(ceolDevice, baseurl, prefs.getMacroNames(), prefs.getMacroValues());
        ceolCommandManager.register(onCeolStatusChangedListener);
//        InitiateStatusCollection(onCeolStatusChangedListener);
    }

    public void volumeUp() {
//        if ( prefs.getUseTelnet()) {
//            TelnetAPIs.TelnetSend( CeolDeviceCommandString.setVolumeUp()+ "\r");
//        } else {
//            ceolWebService.SendCommand(CeolDeviceCommandString.setVolumeUp());
        ceolCommandManager.execute(new CommandMasterVolume(DirectionType.Up));
//        }
    }

    public void volumeDown() {
        ceolCommandManager.execute(new CommandMasterVolume(DirectionType.Down));
    }

    public void skipBackwards() {
        ceolCommandManager.execute(new CommandSkip(DirectionType.Backward));
    }

    public void skipForwards() {
        ceolCommandManager.execute(new CommandSkip(DirectionType.Forward));
    }

    public void performMacro() {
        ceolCommandManager.execute(new CommandMacro(1));
//        ceolCommandManager.execute(new CommandBrowseInto(ceolCommandManager, "Random Pop 2", true));
    }


/*
    private void InitiateApacheTelnet() {
        telnetClient = new TelnetClient();
        // VT100 terminal type will be subnegotiated
        TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
        // WILL SUPPRESS-GA, DO SUPPRESS-GA options
        SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true, true, true);
        // WON'T ECHO, DON'T ECHO
        EchoOptionHandler echoopt = new EchoOptionHandler();
/*        try {
            // set telnet client options
            telnetClient.addOptionHandler(ttopt);
            telnetClient.addOptionHandler(gaopt);
            telnetClient.addOptionHandler(echoopt);

            // connect
            telnetClient.connect(host, Integer.parseInt(port));

            // set the read timeout
            telnetClient.setSoTimeout(READ_TIMEOUT);

            // Initialize the print writer
            outPrint = new PrintWriter(telnetClient.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new TransportException("Could not connect, unable to open telnet session " + e.getMessage());
        }

    }
    */
}
