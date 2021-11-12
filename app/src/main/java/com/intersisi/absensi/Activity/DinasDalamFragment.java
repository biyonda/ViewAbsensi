package com.intersisi.absensi.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.intersisi.absensi.Api.Api;
import com.intersisi.absensi.Api.RetrofitClient;
import com.intersisi.absensi.Helpers.ApiError;
import com.intersisi.absensi.Helpers.ErrorUtils;
import com.intersisi.absensi.R;
import com.intersisi.absensi.Response.BaseResponse;
import com.intersisi.absensi.Session.Session;
import com.intersisi.absensi.Table.Absen;
import com.intersisi.absensi.Table.JadwalHariIni;
import com.intersisi.absensi.Table.Jarak;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DinasDalamFragment extends Fragment {

    LinearLayout btn_absen_masuk, btn_absen_pulang;
    TextView tgl_hadir, absen_masuk, absen_pulang;

    DateFormat dateFormat;
    Date date;

    Session session;
    Api api;
    Call<BaseResponse<Absen>> dataAbsen;
    Call<BaseResponse<Absen>> absenMasuk;
    Call<BaseResponse<Absen>> absenPulang;
    Call<BaseResponse<JadwalHariIni>> getJadwalHariIni;
    Call<BaseResponse<Jarak>> getJarak;
    Boolean sts_masuk = false;
    Boolean sts_pulang = false;
    Boolean sts_dinas_luar = false;
    Boolean sts_jadwal = false;

    String jam_mulai_masuk, jam_sampai_masuk;
    String jam_mulai_pulang, jam_sampai_pulang;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dinas_dalam, container, false);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = new Date();

        btn_absen_masuk = (LinearLayout) view.findViewById(R.id.btn_absen_masuk);
        btn_absen_pulang = (LinearLayout) view.findViewById(R.id.btn_absen_pulang);
        tgl_hadir = view.findViewById(R.id.tgl_hadir);
        absen_masuk = view.findViewById(R.id.absen_masuk);
        absen_pulang = view.findViewById(R.id.absen_pulang);
        tgl_hadir.setText(dateFormat.format(date));

        session = new Session(getContext());
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());
        jadwalHariIni(session.getNip());

        absenMasuk = api.getAbsen("1");
        absenMasuk.enqueue(new Callback<BaseResponse<Absen>>() {
            @Override
            public void onResponse(Call<BaseResponse<Absen>> call, Response<BaseResponse<Absen>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getData().get(0).getDinasLuar().equals("1")) {
                        sts_dinas_luar = true;
                        sts_masuk = true;
                        sts_pulang = true;
                        absen_masuk.setText("Dinas Luar");
                        absen_pulang.setText("Dinas Luar");
                    } else {
                        sts_masuk = true;
                        absen_masuk.setText(response.body().getData().get(0).getScanDate().substring(11));
                    }
                } else {
                    sts_masuk = false;
                    absen_masuk.setText("-");
                    ApiError apiError = ErrorUtils.parseError(response);
//                    Log.d("TAG", "onResponse: " + apiError.getMessage());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Absen>> call, Throwable t) {
                sts_masuk = false;
                absen_masuk.setText("-");
                Log.d("TAG", "onResponse: " + t.getMessage());
            }
        });

        if (sts_dinas_luar) {

        } else {
            absenMasuk = api.getAbsen("2");
            absenMasuk.enqueue(new Callback<BaseResponse<Absen>>() {
                @Override
                public void onResponse(Call<BaseResponse<Absen>> call, Response<BaseResponse<Absen>> response) {
                    if (response.isSuccessful()) {
                        sts_pulang = true;
                        absen_pulang.setText(response.body().getData().get(0).getScanDate().substring(11));
                    } else {
                        sts_pulang = false;
                        absen_pulang.setText("-");
                        ApiError apiError = ErrorUtils.parseError(response);
//                        Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse<Absen>> call, Throwable t) {
                    sts_pulang = false;
                    absen_pulang.setText("-");
                    Toast.makeText(getContext(), "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        btn_absen_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sts_jadwal == false) {
                    Toast.makeText(getContext(), "Tidak ada jadwal hari ini.", Toast.LENGTH_SHORT).show();
                } else {
                    if (sts_masuk) {
                        Toast.makeText(getContext(), "Anda telah absen masuk", Toast.LENGTH_SHORT).show();
                    } else {
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                        Date date = new Date();
                        String jam_skrg = formatter.format(date);
                        System.out.println(jam_skrg);
                        System.out.println(jam_mulai_masuk);
                        System.out.println(jam_sampai_masuk);
                        if (cekAfter(jam_skrg, jam_mulai_masuk) && cekBefore(jam_skrg, jam_sampai_masuk)) {
                            Intent it = new Intent(getContext(), PilihAbsensiActivity.class);
                            it.putExtra("tipe", "masuk");
                            startActivity(it);
                        } else {
                            if (cekAfter(jam_skrg, jam_sampai_pulang)) {
                                Toast.makeText(getContext(), "Sudah melewati jam presensi", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Belum memasuki waktu presensi !!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });

        btn_absen_pulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sts_jadwal == false) {
                    Toast.makeText(getContext(), "Tidak ada jadwal hari ini.", Toast.LENGTH_SHORT).show();
                } else {
                    if (sts_pulang) {
                        Toast.makeText(getContext(), "Anda telah absen pulang", Toast.LENGTH_SHORT).show();
                    } else {
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                        Date date = new Date();
                        String jam_skrg = formatter.format(date);
                        System.out.println(jam_skrg);
                        System.out.println(jam_mulai_pulang);
                        System.out.println(jam_sampai_pulang);
                        if (cekAfter(jam_skrg, jam_mulai_pulang) && cekBefore(jam_skrg, jam_sampai_pulang)) {
                            Intent it = new Intent(getContext(), PilihAbsensiActivity.class);
                            it.putExtra("tipe", "pulang");
                            startActivity(it);
                        } else {
                            Toast.makeText(getContext(), "Belum masuk waktu presensi !!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        return view;
    }

    public void jadwalHariIni(String nip) {
        getJadwalHariIni = api.getJadwalHariIni(session.getNip());
        getJadwalHariIni.enqueue(new Callback<BaseResponse<JadwalHariIni>>() {
            @Override
            public void onResponse(Call<BaseResponse<JadwalHariIni>> call, Response<BaseResponse<JadwalHariIni>> response) {
                if (response.isSuccessful()) {
                    sts_jadwal = true;
                    jam_mulai_masuk = response.body().getData().get(0).getMulaiMasuk();
                    jam_sampai_masuk = response.body().getData().get(0).getSampaiMasuk();
                    jam_mulai_pulang = response.body().getData().get(0).getMulaiPulang();
                    jam_sampai_pulang = response.body().getData().get(0).getSampaiPulang();
                } else {
                    sts_jadwal = false;
                    ApiError apiError = ErrorUtils.parseError(response);
                    Log.d("TAG", "onResponse: " + apiError.getMessage());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<JadwalHariIni>> call, Throwable t) {
                sts_jadwal = false;
                Log.d("TAG", "onResponse: " + t.getMessage());
            }
        });
    }

    private boolean cekBefore(String time, String endtime) {

        String pattern = "H:m:s";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if (date1.before(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean cekAfter(String time, String endtime) {

        String pattern = "H:m:s";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if (date1.after(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}