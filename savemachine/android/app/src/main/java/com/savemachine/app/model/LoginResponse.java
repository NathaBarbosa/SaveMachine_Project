package com.savemachine.app.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("id")    public int id;
    @SerializedName("name")  public String name;
    @SerializedName("email") public String email;
    @SerializedName("token") public String token;
    @SerializedName("error") public String error;
}
