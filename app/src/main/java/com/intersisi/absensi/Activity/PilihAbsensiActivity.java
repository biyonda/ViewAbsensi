package com.intersisi.absensi.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.intersisi.absensi.Api.Api;
import com.intersisi.absensi.Api.RetrofitClient;
import com.intersisi.absensi.Helpers.ApiError;
import com.intersisi.absensi.Helpers.ErrorUtils;
import com.intersisi.absensi.R;
import com.intersisi.absensi.Response.BaseResponse;
import com.intersisi.absensi.Session.Session;
import com.intersisi.absensi.Table.JadwalHariIni;
import com.intersisi.absensi.Table.Jarak;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PilihAbsensiActivity extends AppCompatActivity {

    LinearLayout btn_absen_wajah, btn_qr_code;
    ImageView btn_back;

    DateFormat dateFormat, dateFormat1;
    Date date;

    Session session;
    Api api;
    Call<BaseResponse<JadwalHariIni>> getJadwalHariIni;
    Call<BaseResponse> absenWajah;
    Call<BaseResponse> absenScanQr;
    Call<BaseResponse> cekQr;
    Call<Jarak> getJarak;

    ArrayList<String> shift = new ArrayList<>();
    ArrayList<String> jam_mulai = new ArrayList<>();
    ArrayList<String> jam_selesai = new ArrayList<>();

    String jam_kerja_id, tipe;

    String latitude = "0";
    String longitude = "0";
    String dest = "";
    FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) PilihAbsensiActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    final Location location = task.getResult();
                    if (location != null) {
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());
                    } else {
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                Location location1 = locationResult.getLastLocation();
                                latitude = String.valueOf(location1.getLatitude());
                                longitude = String.valueOf(location1.getLongitude());
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                    dest = latitude + "," + longitude;
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_absensi);

        btn_back = findViewById(R.id.btn_back);
        btn_absen_wajah = findViewById(R.id.btn_absen_wajah);
        btn_qr_code = findViewById(R.id.btn_qr_code);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        dateFormat1 = new SimpleDateFormat("H:m:s");
        date = new Date();

        session = new Session(PilihAbsensiActivity.this);
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(PilihAbsensiActivity.this);

        tipe = getIntent().getStringExtra("tipe");
        jadwalHariIni(session.getNip());

        if (ActivityCompat.checkSelfPermission(PilihAbsensiActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(PilihAbsensiActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(PilihAbsensiActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 105);
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_absen_wajah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String origin = session.getLat() + "," + session.getLng();
                getJarak = api.getJarak(origin, dest, "AIzaSyBhYpivDh3X593xIjPmfgqiMP3eB6KSbZM");
                getJarak.enqueue(new Callback<Jarak>() {
                    @Override
                    public void onResponse(Call<Jarak> call, Response<Jarak> response) {
                        if (response.isSuccessful()) {
                            System.out.println(response.body().getRows().get(0).getElements().get(0).getDistance().getValue());
                            if (response.body().getRows().get(0).getElements().get(0).getDistance().getValue() <= 50) {
                                try {
                                    if (ActivityCompat.checkSelfPermission(PilihAbsensiActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                        Intent intent = new Intent();
                                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(intent, 102);
                                    } else {
                                        ActivityCompat.requestPermissions(PilihAbsensiActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(PilihAbsensiActivity.this, "Anda tidak berada dalam radius Rs. Soebandi !!!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PilihAbsensiActivity.this, "Jarak tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Jarak> call, Throwable t) {
                        Toast.makeText(PilihAbsensiActivity.this, "Jarak tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btn_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String origin = session.getLat() + "," + session.getLng();
                getJarak = api.getJarak(origin, dest, "AIzaSyBhYpivDh3X593xIjPmfgqiMP3eB6KSbZM");
                getJarak.enqueue(new Callback<Jarak>() {
                    @Override
                    public void onResponse(Call<Jarak> call, Response<Jarak> response) {
                        if (response.isSuccessful()) {
                            System.out.println(response.body().getRows().get(0).getElements().get(0).getDistance().getValue());
                            if (response.body().getRows().get(0).getElements().get(0).getDistance().getValue() <= 50) {
                                Intent i = new Intent(PilihAbsensiActivity.this, QrScanner.class);
                                startActivityForResult(i, 1);
                            } else {
                                Toast.makeText(PilihAbsensiActivity.this, "Anda tidak berada dalam radius Rs. Soebandi !!!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PilihAbsensiActivity.this, "Jarak tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Jarak> call, Throwable t) {
                        Toast.makeText(PilihAbsensiActivity.this, "Jarak tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0) {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 102);
        } else {
            Toast.makeText(PilihAbsensiActivity.this, "Butuh akses kamera.", Toast.LENGTH_SHORT);
        }

        if (requestCode == 105 && grantResults.length > 0) {
            getCurrentLocation();
        } else {
            Toast.makeText(PilihAbsensiActivity.this, "Butuh akses lokasi.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                int sts = 0;
                if (tipe.equals("masuk")) {
                    sts = 1;
                } else {
                    sts = 2;
                }

                absenWajah = api.absenWajah(sts + "", jam_kerja_id + "", latitude, longitude, imageToString(photo), "");
                absenWajah.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.isSuccessful()) {
                            Intent intent = new Intent(PilihAbsensiActivity.this, AbsensiBerhasilActivity.class);
                            intent.putExtra("tipe", tipe);
                            startActivity(intent);
                            finish();
                        } else {
                            ApiError apiError = ErrorUtils.parseError(response);
                            Toast.makeText(PilihAbsensiActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(PilihAbsensiActivity.this, "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if (requestCode == 1) {
            if (resultCode == 101) {
//                Toast.makeText(PilihAbsensiActivity.this, data.getExtras().get("content").toString(), Toast.LENGTH_SHORT).show();
                cekQr = api.cekQr(data.getExtras().get("content").toString());
                cekQr.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.isSuccessful()) {
                            int sts = 0;
                            if (tipe.equals("masuk")) {
                                sts = 1;
                            } else {
                                sts = 2;
                            }
                            absenScanQr = api.absenScanQr(sts + "", jam_kerja_id + "", latitude, longitude, "");
                            absenScanQr.enqueue(new Callback<BaseResponse>() {
                                @Override
                                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                                    if (response.isSuccessful()) {
                                        Intent intent = new Intent(PilihAbsensiActivity.this, AbsensiBerhasilActivity.class);
                                        intent.putExtra("tipe", tipe);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        ApiError apiError = ErrorUtils.parseError(response);
                                        Toast.makeText(PilihAbsensiActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<BaseResponse> call, Throwable t) {
                                    Toast.makeText(PilihAbsensiActivity.this, "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            ApiError apiError = ErrorUtils.parseError(response);
                            Toast.makeText(PilihAbsensiActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(PilihAbsensiActivity.this, "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void jadwalHariIni(String nip) {
        getJadwalHariIni = api.getJadwalHariIni(session.getNip());
        getJadwalHariIni.enqueue(new Callback<BaseResponse<JadwalHariIni>>() {
            @Override
            public void onResponse(Call<BaseResponse<JadwalHariIni>> call, Response<BaseResponse<JadwalHariIni>> response) {
                if (response.isSuccessful()) {
                    shift.clear();
                    jam_mulai.clear();
                    jam_selesai.clear();

                    for (int i = 0; i < response.body().getData().size(); i++) {
                        shift.add(response.body().getData().get(i).getNama());
                        if (tipe.equals("masuk")) {
                            jam_mulai.add(response.body().getData().get(i).getMulaiMasuk());
                            jam_selesai.add(response.body().getData().get(i).getSampaiMasuk());
                        } else {
                            jam_mulai.add(response.body().getData().get(i).getMulaiPulang());
                            jam_selesai.add(response.body().getData().get(i).getSampaiPulang());
                        }
                    }

                } else {
                    jam_kerja_id = "";
                    ApiError apiError = ErrorUtils.parseError(response);
                    Toast.makeText(PilihAbsensiActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<JadwalHariIni>> call, Throwable t) {
                jam_kerja_id = "";
                Toast.makeText(PilihAbsensiActivity.this, "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    private boolean cekBefore(String time, String endtime) {

        String pattern = "H:m:s";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if (date1.before(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean cekAfter(String time, String endtime) {

        String pattern = "H:m:s";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if (date1.after(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}