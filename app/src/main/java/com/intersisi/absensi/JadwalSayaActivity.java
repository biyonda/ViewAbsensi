package com.intersisi.absensi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.Calendar;

public class JadwalSayaActivity extends AppCompatActivity {

    ImageView btn_back;
    Spinner bln_spinner, thn_spinner;
    CalendarView calendar_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_saya);

        btn_back = findViewById(R.id.btn_back);
        bln_spinner = findViewById(R.id.bln_spinner);
        thn_spinner = findViewById(R.id.thn_spinner);
        calendar_view = findViewById(R.id.calendar_view);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayAdapter<CharSequence> bulan_spinner = ArrayAdapter.createFromResource(this, R.array.bulan_array, android.R.layout.simple_spinner_item);
        bulan_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bln_spinner.setAdapter(bulan_spinner);

        ArrayAdapter<CharSequence> tahun_spinner = ArrayAdapter.createFromResource(this, R.array.tahun_array, android.R.layout.simple_spinner_item);
        tahun_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        thn_spinner.setAdapter(tahun_spinner);

        String date = "28/8/2021";
        String parts[] = date.split("/");

        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        long milliTime = calendar.getTimeInMillis();

        calendar_view.setDate (milliTime, true, true);

    }
}