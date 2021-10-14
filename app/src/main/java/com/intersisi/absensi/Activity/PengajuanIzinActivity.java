package com.intersisi.absensi.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.intersisi.absensi.R;

import java.util.Calendar;

public class PengajuanIzinActivity extends AppCompatActivity {

    ImageView btn_back;
    Spinner cuti_spinner;
    LinearLayout select_tgl_awal,select_tgl_akhir;
    TextView tgl_awal, tgl_akhir;
    Button btn_simpan, btn_browse;

    final Calendar calendar = Calendar.getInstance();
    int yy = calendar.get(Calendar.YEAR);
    int mm = calendar.get(Calendar.MONTH);
    int dd = calendar.get(Calendar.DAY_OF_MONTH);

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan_izin);

        btn_back = findViewById(R.id.btn_back);
        select_tgl_awal = findViewById(R.id.select_tgl_awal);
        select_tgl_akhir = findViewById(R.id.select_tgl_akhir);
        tgl_awal = findViewById(R.id.tgl_awal);
        tgl_akhir = findViewById(R.id.tgl_akhir);
        btn_browse = findViewById(R.id.btn_browse);
        btn_simpan = findViewById(R.id.btn_simpan);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayAdapter<CharSequence> bulan_spinner = ArrayAdapter.createFromResource(this, R.array.izin_array, android.R.layout.simple_spinner_item);
        bulan_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cuti_spinner.setAdapter(bulan_spinner);

        select_tgl_awal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(PengajuanIzinActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(PengajuanIzinActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String tglAkhir = String.valueOf(year) +"-"+String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth);
                        tgl_akhir.setText(tglAkhir);
                    }
                }, yy, mm, dd);
                datePickerDialog.show();
            }
        });

        btn_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

    }
}