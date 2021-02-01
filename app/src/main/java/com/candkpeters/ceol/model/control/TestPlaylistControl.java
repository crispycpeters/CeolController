package com.candkpeters.ceol.model.control;

import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolModel;

import java.util.Random;

/**
 * Created by crisp on 26/05/2017.
 */

public class TestPlaylistControl extends PlaylistControlBase {

    private final CeolModel ceolModel;
    Random r = new Random();

    public TestPlaylistControl( CeolModel ceolModel) {
        this.ceolModel = ceolModel;
    }

    @Override
    public int getPlaylistLen() {
        if ( ceolModel.inputControl.trackControl.getAudioItem() != null) {
            return 20;
        } else {
            return 0;
        }
    }

    @Override
    public AudioStreamItem getPlaylistAudioItem(int pos) {
        if ( ceolModel.inputControl.trackControl.getAudioItem() != null) {
            AudioStreamItem newItem = new AudioStreamItem();
            newItem.setAudioItem(ceolModel.inputControl.trackControl.getAudioItem());
            newItem.setTitle(newItem.getTitle() + " #" + (pos+1));
            newItem.setId(pos+1);
            return newItem;
        } else {
            return null;
        }
    }

    @Override
    public int getCurrentTrackPosition() {
        return 12;
    }
}
