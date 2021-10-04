package com.intersisi.absensi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class RiwayatKehadiranActivity extends AppCompatActivity {

    ImageView btn_back;
    LinearLayout select_tgl_awal,select_tgl_akhir;
    TextView tgl_awal, tgl_akhir;

    final Calendar calendar = Calendar.getInstance();
    int yy = calendar.get(Calendar.YEAR);
    int mm = calendar.get(Calendar.MONTH);
    int dd = calendar.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_kehadiran);

        btn_back = findViewById(R.id.btn_back);
        select_tgl_awal = findViewById(R.id.select_tgl_awal);
        select_tgl_akhir = findViewById(R.id.select_tgl_akhir);
        tgl_awal = findViewById(R.id.tgl_awal);
        tgl_akhir = findViewById(R.id.tgl_akhir);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        select_tgl_awal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RiwayatKehadiranActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String tglAwal = String.valueOf(year) +"-"+String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth);
                        tgl_awal.setText(tglAwal);
                    }
                }, yy, mm, dd);
                datePickerDialog.show();
            }
        });

        select_tgl_akhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RiwayatKehadiranActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String tglAkhir = String.valueOf(year) +"-"+String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth);
                        tgl_akhir.setText(tglAkhir);
                    }
                }, yy, mm, dd);
                datePickerDialog.show();
            }
        });

    }
}