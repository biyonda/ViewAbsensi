package com.intersisi.absensi.Session;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    Context context;

    public Session(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences("Absensi", context.MODE_PRIVATE);
        this.editor = preferences.edit();
    }

    public void setLoggedIn(Boolean loggedIn) {
        editor.putBoolean("loggedIn", loggedIn);
        editor.commit();
    }

    public void setUserStatus(Boolean loggedIn, String id_user, String name, String email, String token, String otoritas, String jenis_kelamin){
        editor.putBoolean("loggedIn", loggedIn);
        editor.putString("id_user", id_user);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("token", token);
        editor.putString("otoritas", otoritas);
        editor.putString("JNS_KELAMIN", jenis_kelamin);
        editor.commit();
    }

    public String getBaseUrl() {
        return preferences.getString("baseUrl", "192.168.100.235:8000");
    }

}

