package com.intersisi.absensi.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.intersisi.absensi.Api.Api;
import com.intersisi.absensi.Api.RetrofitClient;
import com.intersisi.absensi.Helpers.ApiError;
import com.intersisi.absensi.Helpers.ErrorUtils;
import com.intersisi.absensi.R;
import com.intersisi.absensi.Response.BaseResponse;
import com.intersisi.absensi.Session.Session;
import com.intersisi.absensi.Table.Absen;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AbsensiBerhasilActivity extends AppCompatActivity {

    Button btn_home;
    TextView tipe, jam_absen;

    Session session;
    Api api;
    Call<BaseResponse<Absen>> absen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensi_berhasil);

        tipe = findViewById(R.id.tipe);
        jam_absen = findViewById(R.id.jam_absen);
        btn_home = findViewById(R.id.btn_home);

        if (getIntent().getStringExtra("tipe").equals("masuk")) {
            tipe.setText("DATANG");
            getAbsen("1");
        } else if (getIntent().getStringExtra("tipe").equals("pulang")) {
            tipe.setText("PULANG");
            getAbsen("2");
        } else {
            tipe.setText("Dinas Luar");
            getAbsen("1");
        }

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AbsensiBerhasilActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void getAbsen(String sts){
        session = new Session(AbsensiBerhasilActivity.this);
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());

        absen = api.getAbsen("1");
        absen.enqueue(new Callback<BaseResponse<Absen>>() {
            @Override
            public void onResponse(Call<BaseResponse<Absen>> call, Response<BaseResponse<Absen>> response) {
                if (response.isSuccessful()) {
                    jam_absen.setText(response.body().getData().get(0).getScanDate().substring(11));
                } else {
                    ApiError apiError = ErrorUtils.parseError(response);
                    Toast.makeText(AbsensiBerhasilActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Absen>> call, Throwable t) {
                Toast.makeText(AbsensiBerhasilActivity.this, "Error "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}