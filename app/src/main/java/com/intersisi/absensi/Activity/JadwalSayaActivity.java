package com.intersisi.absensi.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.ik024.calendar_lib.custom.MonthView;
import com.github.ik024.calendar_lib.listeners.MonthViewClickListeners;
import com.intersisi.absensi.Api.Api;
import com.intersisi.absensi.Api.RetrofitClient;
import com.intersisi.absensi.Helpers.ApiError;
import com.intersisi.absensi.Helpers.ErrorUtils;
import com.intersisi.absensi.R;
import com.intersisi.absensi.Response.BaseResponse;
import com.intersisi.absensi.Session.Session;
import com.intersisi.absensi.Table.JadwalHariIni;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JadwalSayaActivity extends AppCompatActivity implements MonthViewClickListeners {

    ImageView btn_back;
    Button lihat_jadwal;
    MonthView monthView;
    RelativeLayout progress;

    Api api;
    Session session;
    Call<BaseResponse<JadwalHariIni>> getJadwalSaya;

    ArrayList<String> shift = new ArrayList<>();
    ArrayList<String> tgl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_saya);

        session = new Session(this);
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());
        getJadwalSaya = api.getJadwalSaya();

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lihat_jadwal = findViewById(R.id.lihat_jadwal);
        monthView = findViewById(R.id.jadwal_saya);
        progress = findViewById(R.id.progress);

        lihat_jadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                List<Date> eventList = new ArrayList<>();
                getJadwalSaya.enqueue(new Callback<BaseResponse<JadwalHariIni>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<JadwalHariIni>> call, Response<BaseResponse<JadwalHariIni>> response) {
                        if (response.isSuccessful()) {
                            eventList.clear();
                            shift.clear();
                            tgl.clear();

                            for (int i = 0; i < response.body().getData().size(); i++) {
                                shift.add(response.body().getData().get(i).getNama());
                                tgl.add(response.body().getData().get(i).getTgl());
                                int y = Integer.parseInt(response.body().getData().get(i).getTgl().substring(0, 4));
                                int m = Integer.parseInt(response.body().getData().get(i).getTgl().substring(5, 7));
                                int d = Integer.parseInt(response.body().getData().get(i).getTgl().substring(8, 10));
                                System.out.println(y+" | "+m+" | "+d);
                                eventList.add(getDate(y, m, d));
                            }

                            progress.setVisibility(View.GONE);
                            monthView.setVisibility(View.VISIBLE);
//                            monthView.setMonthNameTextColor(Color.parseColor("#FF9800"));
//                            monthView.setDaysOfMonthTextColor(Color.parseColor("#FF9800"));
//                            monthView.setDaysOfWeekTextColor(Color.parseColor("#FF9800"));
//                            monthView.setCurrentDayTextColor(Color.parseColor("#FF9800"));
//                            monthView.setCurrentDayTextColor(R.color.colorAccent);
                            monthView.registerClickListener(JadwalSayaActivity.this);
                            monthView.setEventList(eventList);
                        } else {
                            ApiError apiError = ErrorUtils.parseError(response);
                            Toast.makeText(JadwalSayaActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<JadwalHariIni>> call, Throwable t) {
                        Toast.makeText(JadwalSayaActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Override
    public void dateClicked(Date dateClicked) {
//        Toast.makeText(JadwalSayaActivity.this, dateClicked.getDate(), Toast.LENGTH_LONG).show();
        if (tgl.indexOf((dateClicked.getYear()+1900)+"-"+(dateClicked.getMonth()+1)+"-"+dateClicked.getDate()) >= 0 ) {
            Toast.makeText(JadwalSayaActivity.this, "Shift "+ shift.get(tgl.indexOf((dateClicked.getYear()+1900)+"-"+(dateClicked.getMonth()+1)+"-"+dateClicked.getDate())),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(JadwalSayaActivity.this, "Tidak Ada Jadwal", Toast.LENGTH_SHORT).show();
        }
    }
}