package com.candkpeters.ceol.device;

import com.mobprofs.retrofit.converters.SimpleXmlConverter;

import retrofit.RestAdapter;
import retrofit.client.Request;
import retrofit.client.UrlConnectionClient;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by crisp on 02/01/2016.
 */
public final class WebSvcConnectionClient extends UrlConnectionClient {

    @Override protected HttpURLConnection openConnection(Request request) throws IOException {
        HttpURLConnection connection = super.openConnection(request);
        connection.setConnectTimeout(20000);
        connection.setReadTimeout(20000);
        return connection;
    }
}