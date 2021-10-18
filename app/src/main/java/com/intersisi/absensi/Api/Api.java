package com.intersisi.absensi.Api;

import com.intersisi.absensi.Response.BaseResponse;
import com.intersisi.absensi.Response.UserResponse;
import com.intersisi.absensi.Table.Absen;
import com.intersisi.absensi.Table.JadwalHariIni;
import com.intersisi.absensi.Table.Kehadiran;

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

    @FormUrlEncoded
    @POST("getJadwalHariIni")
    Call<BaseResponse<JadwalHariIni>> getJadwalHariIni(
            @Field("nip") String nip
    );

    @FormUrlEncoded
    @POST("getAbsensi")
    Call<BaseResponse<Absen>> getAbsen(
            @Field("sts") String sts
    );

    @FormUrlEncoded
    @POST("absenWajah")
    Call<BaseResponse> absenWajah(
            @Field("status") String status,
            @Field("jam_kerja_id") String jam_kerja_id,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("gambar") String gambar,
            @Field("dinas_luar") String dinas_luar
    );

    @FormUrlEncoded
    @POST("absenScanQr")
    Call<BaseResponse> absenScanQr(
            @Field("status") String status,
            @Field("jam_kerja_id") String jam_kerja_id,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("dinas_luar") String dinas_luar
    );

    @FormUrlEncoded
    @POST("cekQr")
    Call<BaseResponse> cekQr(
            @Field("qr") String qr
    );

    @GET("getRiwayatKehadiran")
    Call<BaseResponse<Kehadiran>> getRiwayatKehadiran();
}
