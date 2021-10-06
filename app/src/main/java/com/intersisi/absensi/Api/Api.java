package com.intersisi.absensi.Api;

import com.intersisi.absensi.Response.BaseResponse;
import com.intersisi.absensi.Response.UserResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("login")
    Call<UserResponse> login(
            @Field("nip") String nip,
            @Field("password") String password,
            @Field("imei") String imei
    );

}
