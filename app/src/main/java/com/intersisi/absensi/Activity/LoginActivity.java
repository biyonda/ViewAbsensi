package com.intersisi.absensi.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.intersisi.absensi.Api.Api;
import com.intersisi.absensi.Api.RetrofitClient;
import com.intersisi.absensi.Helpers.ApiError;
import com.intersisi.absensi.Helpers.ErrorUtils;
import com.intersisi.absensi.R;
import com.intersisi.absensi.Response.UserResponse;
import com.intersisi.absensi.Session.Session;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_READ_PHONE_STATE = 0;
    Button btn_login;
    EditText nip, password;
    ProgressBar progress;
    Context context;

    Api api;
    Session session;
    Call<UserResponse> login;
    String imei = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = LoginActivity.this;

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(context.TELEPHONY_SERVICE);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imei = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            } else {
                if (telephonyManager.getDeviceId() != null) {
                    imei = telephonyManager.getDeviceId();
                } else {
                    imei = Settings.Secure.getString(
                            context.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                }
            }
        }

        session = new Session(this);
        api = RetrofitClient.createService(Api.class);
        nip = findViewById(R.id.nip);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        progress = findViewById(R.id.progress);
        System.out.println(imei);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent login = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(login);
//                finish();
                System.out.println(nip.getText().toString());
                System.out.println(password.getText().toString());
                progress.setVisibility(View.VISIBLE);
                login = api.login(nip.getText().toString(), password.getText().toString(), imei);
                login.enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if (response.isSuccessful()) {
                            session.setUserStatus(true, response.body().getUser().getId().toString(), response.body().getUser().getNip(), response.body().getUser().getNama(),
                                    response.body().getUser().getApiToken(), response.body().getUser().getBagianId(), response.body().getUser().getNamaBagian(), response.body().getUser().getInisial());
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            Toast.makeText(LoginActivity.this, "Selamat datang "+response.body().getUser().getNama(), Toast.LENGTH_SHORT).show();
                            progress.setVisibility(View.GONE);
                        } else {
                            ApiError apiError = ErrorUtils.parseError(response);
                            Toast.makeText(LoginActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Error, "+t.getMessage(), Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    System.out.println("need permission");
                }
                break;

            default:
                break;
        }
    }
}