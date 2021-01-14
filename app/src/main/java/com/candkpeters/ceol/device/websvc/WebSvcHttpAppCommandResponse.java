package com.candkpeters.ceol.device.websvc;

import com.candkpeters.ceol.device.websvc.WebSvcHttpResponseText;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.util.Dictionary;

/**
 * Created by crisp on 02/01/2016.
 */
@Root(name="rx", strict = false)
public class WebSvcHttpAppCommandResponse {

//    @ElementList(entry = "cmd", inline = true )
//    private List<CeolAppCommandResponseCmd> cmds;

    @Element( required = false)
    @Path("cmd[1]")
    public String power;

    @Element( required = false)
    @Path("cmd[2]")
    public String volume;

    @Element( required = false)
    @Path("cmd[2]")
    public String disptype;

    @Element( required = false)
    @Path("cmd[2]")
    String dispvalue;

    //
    // cmd[5] is variable, based on query type
    //

    // Netserver
    @Element( required = false)
    @Path("cmd[3]")
    public String type;

    @ElementList(required = false, entry = "text",inline = true)
    @Path("cmd[3]")
    Dictionary<WebSvcHttpResponseText> texts;

    @Element( required = false)
    @Path("cmd[3]")
    String playstatus;
                                    // CD: REW, FF, PLAY, STOP, PAUSE(?)
                                    // Netserver: Play, ...

    @Element( required = false)
    @Path("cmd[3]")
    public String playcontents;

    @Element( required = false)
    @Path("cmd[3]")
    String listmax;

    @Element( required = false)
    @Path("cmd[3]")
    String listposition;

    @Element( required = false)
    @Path("cmd[3]")
    public String repeat;           // CD, Netserver: OFF, ONE or ALL

    @Element( required = false)
    @Path("cmd[3]")
    public String shuffle;          // CD, Netserver: OFF or ON

    @Element( required = false)
    @Path("cmd[3]")
    public String discstatus;       // CD: 1

    @Element( required = false)
    @Path("cmd[3]")
    public String trackno;          // CD 01, 02 ...

    @Element( required = false)
    @Path("cmd[3]")
    String band;             // Tuner: Tuner, AM

    @Element( required = false)
    @Path("cmd[3]")
    String frequency;        // Tuner: 92.50, 522

    @Element( required = false)
    @Path("cmd[3]")
    public String name;             // Tuner: BBC R4

    @Element( required = false)
    @Path("cmd[3]")
    String automanual;        // Tuner: AUTO

    // Due to a CEOL bug, we need to cope with this field not being returned, so keep this as last cmd element
    // (Scenario is switching to RADIO from SPOTIFY)
    @Element( required = false)
    @Path("cmd[4]")
    String source = "";

    // TODO - Add other entries for CD: foldername, filename, artistname, albumname, songname, time

//    @Element( name = "text", required = false )
//    @Path("cmd[5][text[@id='scridValue']]")
//    public String scridValue;

}
