package com.intersisi.absensi.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.intersisi.absensi.Api.Api;
import com.intersisi.absensi.Api.RetrofitClient;
import com.intersisi.absensi.R;
import com.intersisi.absensi.Response.BaseResponse;
import com.intersisi.absensi.Session.Session;
import com.intersisi.absensi.Table.JadwalHariIni;

import java.io.ByteArrayOutputStream;


public class EditProfilActivity extends AppCompatActivity {

    EditText password_pengguna;
    Button btn_browse, btn_simpan;
    ImageView foto_pengguna;

    Session session;
    Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        btn_browse = findViewById(R.id.btn_browse);
        foto_pengguna = findViewById(R.id.foto_pengguna);
        password_pengguna = findViewById(R.id.password_pengguna);

        session = new Session(this);
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());


        btn_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ActivityCompat.checkSelfPermission(EditProfilActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        Intent intent = new Intent();
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 102);
                    } else {
                        ActivityCompat.requestPermissions(EditProfilActivity.this,new String[]{Manifest.permission.CAMERA}, 100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intent);
        } else {
            Toast.makeText(EditProfilActivity.this, "Permisson Dennied.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 102){
            if(resultCode == RESULT_OK){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                foto_pengguna.setImageBitmap(photo);
            }
        }
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }
}