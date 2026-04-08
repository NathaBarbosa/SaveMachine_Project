package com.savemachine.app.model;
import com.google.gson.annotations.SerializedName;
public class MessageResponse {
    @SerializedName("message") public String message;
    @SerializedName("error")   public String error;
}
