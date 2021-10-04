package com.intersisi.absensi.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.intersisi.absensi.R;

public class DinasDalamFragment extends Fragment {

    LinearLayout btn_pilih_absensi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dinas_dalam, container, false);

        btn_pilih_absensi = (LinearLayout) view.findViewById(R.id.btn_pilih_absensi);

        btn_pilih_absensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), PilihAbsensiActivity.class));
            }
        });

        return view;
    }
}