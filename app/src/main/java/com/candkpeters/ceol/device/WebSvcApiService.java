package com.candkpeters.ceol.device;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.EncodedPath;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.mime.TypedString;

/**
 * Created by crisp on 02/01/2016.
 */
public interface WebSvcApiService {

    @POST("/goform/AppCommand.xml")
    WebSvcHttpResponse appCommand(@Body WebSvcHttpRequest appRequest);

    @POST("/goform/AppCommand.xml")
    void appCommandAsync_old(@Body WebSvcHttpRequest appRequest, Callback<WebSvcHttpResponse> cb);

    @POST("/goform/AppCommand.xml")
    void appCommandAsync(@Body TypedString appRequest, Callback<WebSvcHttpResponse> cb);

    @GET("/goform/formiPhoneAppDirect.xml{command}")
    void appDirectCommandAsync(@EncodedPath("command") String command, Callback<Void> cb);

}
