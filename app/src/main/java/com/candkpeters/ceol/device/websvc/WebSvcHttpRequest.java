package com.candkpeters.ceol.device.websvc;

import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.HashMap;

/**
 * Created by crisp on 02/01/2016.
 */
@Root(name="tx")
public class WebSvcHttpRequest {

    @ElementMap(entry="cmd", key="id", attribute = true, inline = true)
    private HashMap<Integer, String> cmds;

    public WebSvcHttpRequest() {
        cmds = new HashMap<Integer, String>();
    }

    public void setCmd( int id, String cmd) {
        cmds.remove(id);
        cmds.put(id,cmd);
    }
}
