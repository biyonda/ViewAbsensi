package com.intersisi.absensi.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.intersisi.absensi.Api.Api;
import com.intersisi.absensi.Api.RetrofitClient;
import com.intersisi.absensi.Helpers.ApiError;
import com.intersisi.absensi.Helpers.ErrorUtils;
import com.intersisi.absensi.R;
import com.intersisi.absensi.Response.BaseResponse;
import com.intersisi.absensi.Session.Session;
import com.intersisi.absensi.Table.Absen;
import com.intersisi.absensi.Table.JadwalHariIni;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DinasLuarFragment extends Fragment implements OnMapReadyCallback {

    private static final int RESULT_OK = -1;
    LinearLayout btn_absen;

    FusedLocationProviderClient fusedLocationProviderClient;
    Session session;
    Api api;
    Call<BaseResponse<JadwalHariIni>> getJadwalHariIni;
    Call<BaseResponse> absenWajah;
    Call<BaseResponse<Absen>> absenMasuk;

    double latitude = 0;
    double longitude = 0;
    String jam_kerja_id = "";
    private GoogleMap mMap;

    TextView jam_absen, lokasi, koordinat;
    AppCompatImageView gambar;

    Boolean sts_masuk = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dinas_luar, container, false);

        jam_absen = view.findViewById(R.id.jam_absen);
        lokasi = view.findViewById(R.id.lokasi);
        koordinat = view.findViewById(R.id.koordinat);
        gambar = view.findViewById(R.id.gambar);

        session = new Session(getContext());
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());
        jadwalHariIni(session.getNip());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        Places.initialize(getContext(), R.string.google_maps_key+"");

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        absenMasuk = api.getAbsen("1");
        absenMasuk.enqueue(new Callback<BaseResponse<Absen>>() {
            @Override
            public void onResponse(Call<BaseResponse<Absen>> call, Response<BaseResponse<Absen>> response) {
                if (response.isSuccessful()) {
                    sts_masuk = true;
                    if (response.body().getData().get(0).getDinasLuar().equals("1")) {
                        latitude = Double.parseDouble(response.body().getData().get(0).getLat());
                        longitude = Double.parseDouble(response.body().getData().get(0).getLng());
                        jam_absen.setText(response.body().getData().get(0).getScanDate().substring(11));
                        lokasi.setText("Lokasi");
                        koordinat.setText(latitude+", "+longitude);

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.signature(
                                new ObjectKey(String.valueOf(System.currentTimeMillis())));
                        Glide.with(getContext())
                                .setDefaultRequestOptions(requestOptions)
                                .load("http://" + session.getBaseUrl() + "/storage/images/" + response.body().getData().get(0).getFotoWajah())
                                .into(gambar);

                        mMap.clear();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
                        mMap.addMarker(new MarkerOptions()
                                .title("Lokasi Saat Ini")
                                .position(new LatLng(latitude, longitude))
                                .snippet("......"));
                    } else {
                        jam_absen.setText("-");
                        lokasi.setText("-");
                        koordinat.setText("-");
                    }
                } else {
                    sts_masuk = false;
                    jam_absen.setText("-");
                    lokasi.setText("-");
                    koordinat.setText("-");
                    ApiError apiError = ErrorUtils.parseError(response);
                    Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Absen>> call, Throwable t) {
                sts_masuk = false;
                jam_absen.setText("-");
                lokasi.setText("-");
                koordinat.setText("-");
                Toast.makeText(getContext(), "Error "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btn_absen = view.findViewById(R.id.btn_pilih_absensi);
        btn_absen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jam_kerja_id.equals("")) {
                    Toast.makeText(getContext(), "Anda tidak ada jadwal hari ini", Toast.LENGTH_SHORT).show();
                } else if(sts_masuk) {
                    Toast.makeText(getContext(), "Anda telah absen", Toast.LENGTH_SHORT).show();
                } else {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
                        getCurrentLocation();
                    } else {
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 105);
                    }

                    try {
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                            Intent intent = new Intent();
                            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 102);
                        } else {
                            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA}, 100);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return view;
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    final Location location = task.getResult();
                    if (location != null){
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    } else {
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                Location location1 = locationResult.getLastLocation();
                                latitude = location1.getLatitude();
                                longitude = location1.getLongitude();
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                    System.out.println(latitude+", "+longitude);
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 102);
        } else {
            Toast.makeText(getContext(), "Butuh akses kamera.", Toast.LENGTH_SHORT);
        }

        if (requestCode == 105 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
            getCurrentLocation();
        } else {
            Toast.makeText(getContext(), "Butuh akses lokasi.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 102){
            if(resultCode == RESULT_OK){
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                absenWajah = api.absenWajah("1", jam_kerja_id+"", latitude+"", longitude+"", imageToString(photo), "1");
                absenWajah.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.isSuccessful()) {
                            Intent intent = new Intent(getContext(), AbsensiBerhasilActivity.class);
                            intent.putExtra("tipe", "Dinas Luar");
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            ApiError apiError = ErrorUtils.parseError(response);
                            Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Error "+t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    jam_kerja_id = response.body().getData().get(0).getJamKerjaId();
                } else {
                    jam_kerja_id = "";
                    ApiError apiError = ErrorUtils.parseError(response);
                    Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<JadwalHariIni>> call, Throwable t) {
                jam_kerja_id = "";
                Toast.makeText(getContext(), "Error "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.clear();
        LatLng rsds = new LatLng(-8.150933,113.7152505);
        mMap.addMarker(new MarkerOptions().position(rsds).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rsds, 15.0f));

        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}