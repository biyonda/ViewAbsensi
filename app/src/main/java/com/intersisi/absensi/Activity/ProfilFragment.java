package com.intersisi.absensi.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilFragment extends Fragment {

    RelativeLayout btn_logout, btn_edit;
    TextView nama_pengguna, no_pegawai, jabatan_pengguna, shift;
    ImageView foto_pengguna;

    Session session;
    Api api;
    Call<BaseResponse<JadwalHariIni>> getJadwalHariIni;
//    Call<BaseResponse> logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        session = new Session(getContext());
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());

        nama_pengguna = view.findViewById(R.id.nama_pengguna);
        btn_edit = view.findViewById(R.id.btn_edit);
        no_pegawai = view.findViewById(R.id.no_pegawai);
        jabatan_pengguna = view.findViewById(R.id.jabatan_pengguna);
        shift = view.findViewById(R.id.shift);
        btn_logout = view.findViewById(R.id.btn_logout);

        shift.setText(session.getShift());

        nama_pengguna.setText(session.getNama());
        no_pegawai.setText(session.getNip());
        jabatan_pengguna.setText(session.getNamaBagian());

        jadwalHariIni(session.getNip());

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), EditProfilActivity.class));
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.setUserStatus(false, "","", "", "", "", "", "", "", "");
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
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
//                    shift.setText(TextUtils.join(", ", jadwalku));

                } else {
//                    shift.setText("-");
                    ApiError apiError = ErrorUtils.parseError(response);
                    Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<JadwalHariIni>> call, Throwable t) {
//                shift.setText("-");
                Toast.makeText(getContext(), "Error "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}