package com.candkpeters.ceol.device;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

/**
 * Created by crisp on 02/01/2016.
 */
@Root(name="item", strict = false)
public class WebSvcHttpStatusLiteResponse {

//    @ElementList(entry = "cmd", inline = true )
//    private List<CeolAppCommandResponseCmd> cmds;

    @Element( required = false, name = "value")
    @Path("Power")
    public String power;

    @Element( required = false, name = "value")
    @Path("Mute")
    public String mute;

    @Element( required = false, name = "value")
    @Path("InputFuncSelect")
    public String inputFunc;

}
