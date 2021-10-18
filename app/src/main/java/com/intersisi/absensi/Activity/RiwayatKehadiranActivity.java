package com.intersisi.absensi.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.intersisi.absensi.Api.Api;
import com.intersisi.absensi.Api.RetrofitClient;
import com.intersisi.absensi.Helpers.ApiError;
import com.intersisi.absensi.Helpers.ErrorUtils;
import com.intersisi.absensi.R;
import com.intersisi.absensi.Response.BaseResponse;
import com.intersisi.absensi.Session.Session;
import com.intersisi.absensi.Table.Kehadiran;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiwayatKehadiranActivity extends AppCompatActivity {

    ImageView btn_back;
    LinearLayout select_tgl_awal, select_tgl_akhir;
    TextView tgl_awal, tgl_akhir;
    Button lihat_riwayat;
    ListView list_riwayat_kehadiran;

    final Calendar calendar = Calendar.getInstance();
    int yy = calendar.get(Calendar.YEAR);
    int mm = calendar.get(Calendar.MONTH);
    int dd = calendar.get(Calendar.DAY_OF_MONTH);

    ArrayList<String> scan_date = new ArrayList<>();
    ArrayList<String> status = new ArrayList<>();
    ArrayList<String> dinas_luar = new ArrayList<>();

    Session session;
    Api api;
    Call<BaseResponse<Kehadiran>> getRiwayatKehadiran;
    AdapterRiwayatKehadiran adapterRiwayatKehadiran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_kehadiran);

        btn_back = findViewById(R.id.btn_back);
        select_tgl_awal = findViewById(R.id.select_tgl_awal);
        select_tgl_akhir = findViewById(R.id.select_tgl_akhir);
//        tgl_awal = findViewById(R.id.tgl_awal);
//        tgl_akhir = findViewById(R.id.tgl_akhir);
        lihat_riwayat = findViewById(R.id.lihat_riwayat);
        list_riwayat_kehadiran = findViewById(R.id.list_riwayat_kehadiran);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        session = new Session(this);
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());

        lihat_riwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRiwayatKehadiran = api.getRiwayatKehadiran();
                getRiwayatKehadiran.enqueue(new Callback<BaseResponse<Kehadiran>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Kehadiran>> call, Response<BaseResponse<Kehadiran>> response) {
                        if (response.isSuccessful()) {
                            scan_date.clear();
                            status.clear();
                            dinas_luar.clear();

                            for (int i = 0; i < response.body().getData().size(); i++) {
                                scan_date.add(response.body().getData().get(i).getScanDate());
                                status.add(response.body().getData().get(i).getStatus());
                                dinas_luar.add(response.body().getData().get(i).getDinasLuar().toString());
                            }

                            adapterRiwayatKehadiran = new AdapterRiwayatKehadiran(RiwayatKehadiranActivity.this, scan_date, status, dinas_luar);
                            list_riwayat_kehadiran.setAdapter(adapterRiwayatKehadiran);
                            adapterRiwayatKehadiran.notifyDataSetChanged();
                        } else {
                            ApiError apiError = ErrorUtils.parseError(response);
                            Toast.makeText(RiwayatKehadiranActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Kehadiran>> call, Throwable t) {
                        Toast.makeText(RiwayatKehadiranActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

//        select_tgl_awal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(RiwayatKehadiranActivity.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        String tglAwal = String.valueOf(year) +"-"+String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth);
//                        tgl_awal.setText(tglAwal);
//                    }
//                }, yy, mm, dd);
//                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
//                datePickerDialog.show();
//            }
//        });
//
//        select_tgl_akhir.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(RiwayatKehadiranActivity.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        String tglAkhir = String.valueOf(year) +"-"+String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth);
//                        tgl_akhir.setText(tglAkhir);
//                    }
//                }, yy, mm, dd);
//                datePickerDialog.show();
//            }
//        });

    }
}