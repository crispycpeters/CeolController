package com.candkpeters.ceol.device;

import com.candkpeters.ceol.view.Prefs;

/**
 * Created by crisp on 04/05/2017.
 */

public abstract class GathererBase {

    public abstract void start(Prefs prefs);

    public abstract void pause();
}
