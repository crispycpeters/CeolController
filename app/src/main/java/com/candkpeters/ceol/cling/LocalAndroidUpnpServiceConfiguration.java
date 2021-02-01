package com.candkpeters.ceol.cling;

import android.content.Context;

import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;

/**
 * Created by crisp on 04/07/2017.
 */

public class LocalAndroidUpnpServiceConfiguration extends AndroidUpnpServiceConfiguration {

    public final Context context;

    public LocalAndroidUpnpServiceConfiguration( Context context) {
        this.context = context;
    }
}
