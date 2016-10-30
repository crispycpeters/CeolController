package com.candkpeters.ceol.device;

import com.mobprofs.retrofit.converters.SimpleXmlConverter;
import retrofit.RestAdapter;

/**
 * Created by crisp on 02/01/2016.
 */
public class WebSvcGenerator {

    public static WebSvcApiService createService(String endpoint) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(endpoint)
//                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new SimpleXmlConverter())
                .setClient(new WebSvcConnectionClient())
                .build();

        return restAdapter.create(WebSvcApiService.class);
    }
}