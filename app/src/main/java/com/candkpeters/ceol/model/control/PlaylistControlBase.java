package com.candkpeters.ceol.model.control;

import com.candkpeters.ceol.model.AudioStreamItem;

/**
 * Created by crisp on 03/05/2017.
 */

public abstract class PlaylistControlBase extends ControlBase {

    private static final String TAG = "NavigatorControlBase";

    @Override
    protected boolean copyFrom(ControlBase newControl) {
        return false;
    }

    public abstract int getPlaylistLen();
    public abstract AudioStreamItem getPlaylistAudioItem(int pos);

}
