package com.candkpeters.ceol.device;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.EncodedPath;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Streaming;
import retrofit.mime.TypedString;

/**
 * Created by crisp on 02/01/2016.
 */
interface WebSvcApiService {

/*
    @POST("/goform/AppCommand.xml")
    WebSvcHttpAppCommandResponse appCommand(@Body WebSvcHttpRequest appRequest);

    @POST("/goform/AppCommand.xml")
    void appCommandAsync_old(@Body WebSvcHttpRequest appRequest, Callback<WebSvcHttpAppCommandResponse> cb);
*/

    @GET("/goform/formMainZone_MainZoneXmlStatusLite.xml")
    WebSvcHttpStatusLiteResponse appStatusLite();

    @GET("/goform/formMainZone_MainZoneXmlStatusLite.xml")
    void appStatusLiteAsync(Callback<WebSvcHttpStatusLiteResponse> cb);

    @POST("/goform/AppCommand.xml")
    WebSvcHttpAppCommandResponse appCommand(@Body TypedString appRequest);

    @POST("/goform/AppCommand.xml")
    void appCommandAsync(@Body TypedString appRequest, Callback<WebSvcHttpAppCommandResponse> cb);

    @GET("/goform/formiPhoneAppDirect.xml{command}")
    void appDirectCommandAsync(@EncodedPath("command") String command, Callback<Void> cb);

    @GET("/NetAudio/art.asp-jpg{command}")
    @Streaming
    void appGetImageAsync(@EncodedPath("command") String command, Callback<Response> cb);

    @GET("/NetAudio/art.asp-jpg")
    @Streaming
    Response appGetImage();

}
