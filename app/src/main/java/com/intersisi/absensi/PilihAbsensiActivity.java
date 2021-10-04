package com.intersisi.absensi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PilihAbsensiActivity extends AppCompatActivity {

    LinearLayout btn_absen_wajah, btn_qr_code;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_absensi);

        btn_back = findViewById(R.id.btn_back);
        btn_absen_wajah = findViewById(R.id.btn_absen_wajah);
        btn_qr_code = findViewById(R.id.btn_qr_code);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_absen_wajah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PilihAbsensiActivity.this, AbsensiBerhasilActivity.class);
                startActivity(intent);
            }
        });

        btn_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PilihAbsensiActivity.this, AbsensiGagalActivity.class);
                startActivity(intent);
            }
        });

    }
}