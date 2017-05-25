package com.candkpeters.ceol.model.control;

import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.ObservedControlType;

/**
 * Created by crisp on 03/05/2017.
 */

public abstract class PlaylistControlBase extends ControlBase {

    private static final String TAG = "NavigatorControlBase";

    public PlaylistControlBase() {
        super(ObservedControlType.Playlist);
    }

    @Override
    protected boolean copyFrom(ControlBase newControl) {
        return false;
    }

    public abstract int getPlaylistLen();
    public abstract AudioStreamItem getPlaylistAudioItem(int pos);

}
