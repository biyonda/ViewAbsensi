package com.intersisi.absensi.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.intersisi.absensi.Table.User;

public class UserResponse {

    @SerializedName("user")
    @Expose
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
