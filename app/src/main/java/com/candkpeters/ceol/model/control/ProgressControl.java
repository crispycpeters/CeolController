package com.candkpeters.ceol.model.control;

import com.candkpeters.ceol.model.ObservedControlType;

/**
 * Created by crisp on 03/05/2017.
 */

public class ProgressControl extends ControlBase {

    private static final String TAG = "ProgressControl";
    private int progress = 0;

    public ProgressControl() {
        super(ObservedControlType.Progress);
    }

    public boolean updateProgress( long progress) {

        boolean result = false;

        if ( this.progress != progress) {
            // NOTE we are excluding any really big numbers!
            this.progress = (int)progress;
            result = true;
        }

        return result;
    }

    @Override
    protected boolean copyFrom(ControlBase newControl) {
        boolean hasChanged = false;
        if ( newControl != null && newControl instanceof ProgressControl) {
            ProgressControl newProgressControl = (ProgressControl)newControl;
            if (this.progress != newProgressControl.progress) {
                this.progress = newProgressControl.progress;
                hasChanged = true;
            }
        }
        return hasChanged;
    }

    public int getProgress() {
        return progress;
    }

}
