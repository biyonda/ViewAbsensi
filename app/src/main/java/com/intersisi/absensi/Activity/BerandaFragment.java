package com.intersisi.absensi.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.intersisi.absensi.Api.Api;
import com.intersisi.absensi.Api.RetrofitClient;
import com.intersisi.absensi.Helpers.ApiError;
import com.intersisi.absensi.Helpers.ErrorUtils;
import com.intersisi.absensi.R;
import com.intersisi.absensi.Response.BaseResponse;
import com.intersisi.absensi.Session.Session;
import com.intersisi.absensi.Table.JadwalHariIni;

import java.util.ArrayList;
import java.util.Collections;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BerandaFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout btn_jadwal_saya, btn_riwayat_kehadiran, btn_pengajuan_izin;
    Handler handler = new Handler();

    TextView nama_pengguna, nip_pengguna, jabatan_pengguna, shift_pengguna;
    ImageView img_profil;
    Session session;
    Api api;
    Call<BaseResponse<JadwalHariIni>> getJadwalHariIni;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_beranda, container, false);

        session = new Session(getContext());
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        btn_jadwal_saya = (LinearLayout) view.findViewById(R.id.btn_jadwal_saya);
        btn_riwayat_kehadiran = (LinearLayout) view.findViewById(R.id.btn_riwayat_kehadiran);
        btn_pengajuan_izin = (LinearLayout) view.findViewById(R.id.btn_pengajuan_izin);
        img_profil = (ImageView) view.findViewById(R.id.img_profil);

        nama_pengguna = view.findViewById(R.id.nama_pengguna);
        nip_pengguna = view.findViewById(R.id.nip_pengguna);
        jabatan_pengguna = view.findViewById(R.id.jabatan_pengguna);
        shift_pengguna = view.findViewById(R.id.shift_pengguna);
        shift_pengguna.setText(session.getShift());

        nama_pengguna.setText(session.getNama());
        nip_pengguna.setText(session.getNip());
        jabatan_pengguna.setText(session.getNamaBagian());

        jadwalHariIni(session.getNip());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        btn_jadwal_saya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), JadwalSayaActivity.class));
            }
        });

        btn_riwayat_kehadiran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RiwayatKehadiranActivity.class));
            }
        });

        btn_pengajuan_izin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), PengajuanIzinActivity.class));
            }
        });

        if (session.getJenkel().equalsIgnoreCase("laki-laki")) {
            img_profil.setBackgroundResource(R.drawable.profile_photo_l);
        } else if(session.getJenkel().equalsIgnoreCase("perempuan")) {
            img_profil.setBackgroundResource(R.drawable.profile_photo_p);
        } else {
            img_profil.setBackgroundResource(R.drawable.profile_photo_default);
        }

        nama_pengguna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputBox(session.getNip());
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
                    ArrayList<String> jadwalku = new ArrayList<>();
                    jadwalku.clear();

                    for (int i = 0; i < response.body().getData().size(); i++) {
                        jadwalku.add(response.body().getData().get(i).getNama());
                    }

                    Collections.reverse(jadwalku);
//                    shift_pengguna.setText(TextUtils.join(", ", jadwalku));
                } else {
//                    shift_pengguna.setText("-");
//                    ApiError apiError = ErrorUtils.parseError(response);
//                    Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<JadwalHariIni>> call, Throwable t) {
//                shift_pengguna.setText("-");
                Toast.makeText(getContext(), "Error "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showInputBox(String tmp_nip) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Update User");
        View v = getLayoutInflater().inflate(R.layout.adapter_qr, null);
        dialog.setContentView(v);
//        System.out.println(oldItem);
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView close, nip;
        ImageView qr;

        close = v.findViewById(R.id.close);
        nip = v.findViewById(R.id.nip);
        qr = v.findViewById(R.id.qr);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        QRGEncoder qrgEncoder = new QRGEncoder(tmp_nip, null, QRGContents.Type.TEXT, 200);
        Bitmap qrBits = qrgEncoder.getBitmap();
        qr.setImageBitmap(qrBits);

        nip.setText(tmp_nip);

        dialog.show();
    }
}