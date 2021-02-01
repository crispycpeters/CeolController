package com.candkpeters.ceol.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.util.Dictionary;

/**
 * Created by crisp on 23/04/2017.
 */
@Root(name="TrackList", strict = false)
public class TrackList {

    @ElementList(inline = true, type=TrackListEntry.class)
    public Dictionary<TrackListEntry> entries;

}
