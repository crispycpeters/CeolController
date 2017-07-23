package com.candkpeters.ceol.cling;

import android.content.Context;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.android.AndroidRouter;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.transport.Router;

/**
 * Created by crisp on 04/07/2017.
 */

public class LocalUpnpServiceImpl extends UpnpServiceImpl {

    public LocalUpnpServiceImpl(UpnpServiceConfiguration configuration, RegistryListener listener ) {
        super(configuration,listener);
    }

    @Override
    protected Router createRouter(ProtocolFactory protocolFactory, Registry registry) {
        return LocalUpnpServiceImpl.this.createRouter(
                getConfiguration(),
                protocolFactory,
                ((LocalAndroidUpnpServiceConfiguration)getConfiguration()).context
        );
    }

    @Override
    public synchronized void shutdown() {
        // First have to remove the receiver, so Android won't complain about it leaking
        // when the main UI thread exits.
        ((AndroidRouter)getRouter()).unregisterBroadcastReceiver();

        // Now we can concurrently run the Cling shutdown code, without occupying the
        // Android main UI thread. This will complete probably after the main UI thread
        // is done.
        super.shutdown(true);
    }

    protected AndroidRouter createRouter(UpnpServiceConfiguration configuration,
                                         ProtocolFactory protocolFactory,
                                         Context context) {
        return new AndroidRouter(configuration, protocolFactory, context);
    }
}
