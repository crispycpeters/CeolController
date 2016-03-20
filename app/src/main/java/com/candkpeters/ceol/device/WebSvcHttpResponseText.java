package com.candkpeters.ceol.device;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.util.Entry;

import java.util.Map;

/**
 * Created by crisp on 02/01/2016.
 */
@Root(name="text", strict = false)
public class WebSvcHttpResponseText implements Entry {

    @Attribute( required = false)
    public String id;

    @Attribute( required = false)
    public String flag;

    @Text( required = false)
    public String text;

    @Override
    public String getName() {
        return id;
    }

    @Override
    public String toString() {
        return text;
    }
}
