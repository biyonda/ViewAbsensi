package com.example.viewabsensi;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.widget.LinearLayout;

public class BerandaFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout btn_jadwal_saya, btn_riwayat_kehadiran, btn_pengajuan_izin;
    Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_beranda, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        btn_jadwal_saya = (LinearLayout) view.findViewById(R.id.btn_jadwal_saya);
        btn_riwayat_kehadiran = (LinearLayout) view.findViewById(R.id.btn_riwayat_kehadiran);
        btn_pengajuan_izin = (LinearLayout) view.findViewById(R.id.btn_pengajuan_izin);

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

        return view;
    }
}