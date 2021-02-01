package com.candkpeters.ceol.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.util.Entry;

/**
 * Created by crisp on 23/04/2017.
 */
@Root(name="Entry", strict = false)
public class TrackListEntry implements Entry {

    @Element( name="Id")
    public String id;

    @Element( name="Uri")
    public String uri;

    @Element( name="Metadata")
    public String metadata;

    @Override
    public String getName() {
        return id;
    }
}
