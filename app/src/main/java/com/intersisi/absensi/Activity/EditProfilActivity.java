package com.intersisi.absensi.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.intersisi.absensi.Api.Api;
import com.intersisi.absensi.Api.RetrofitClient;
import com.intersisi.absensi.R;
import com.intersisi.absensi.Response.BaseResponse;
import com.intersisi.absensi.Session.Session;
import com.intersisi.absensi.Table.JadwalHariIni;

import retrofit2.Call;

public class EditProfilActivity extends AppCompatActivity {

    EditText nama_pengguna, no_pengguna, password_pengguna;
    Spinner shift_spinner;
    Button btn_simpan;

    Session session;
    Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        nama_pengguna = findViewById(R.id.nama_pengguna);
        no_pengguna = findViewById(R.id.no_pengguna);
        password_pengguna = findViewById(R.id.password_pengguna);

        ArrayAdapter<CharSequence> kelshift_spinner = ArrayAdapter.createFromResource(this, R.array.shift_array, android.R.layout.simple_spinner_item);
        kelshift_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shift_spinner.setAdapter(kelshift_spinner);

        session = new Session(this);
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());

        nama_pengguna.setText(session.getNama());
        no_pengguna.setText(session.getNip());

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}