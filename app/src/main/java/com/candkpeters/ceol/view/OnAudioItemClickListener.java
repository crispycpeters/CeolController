package com.candkpeters.ceol.view;

import com.candkpeters.ceol.model.AudioStreamItem;

/**
 * Created by crisp on 27/05/2017.
 */

public interface OnAudioItemClickListener {
    void onAudioItemClick(AudioStreamItem item, boolean isCurrentTrack);
}
