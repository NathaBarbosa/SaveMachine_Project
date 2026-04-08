package com.savemachine.app.network;

import com.savemachine.app.model.LoginRequest;
import com.savemachine.app.model.LoginResponse;
import com.savemachine.app.model.Machine;
import com.savemachine.app.model.AddMachineRequest;
import com.savemachine.app.model.PredictRequest;
import com.savemachine.app.model.PredictResponse;
import com.savemachine.app.model.HistoryItem;
import com.savemachine.app.model.MessageResponse;
import com.savemachine.app.model.RegisterRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<MessageResponse> register(@Body RegisterRequest request);

    @GET("machines")
    Call<List<Machine>> getMachines();

    @POST("machines")
    Call<Machine> addMachine(@Body AddMachineRequest request);

    @DELETE("machines/{machine_id}")
    Call<MessageResponse> deleteMachine(@Path("machine_id") String machineId);

    @POST("predict")
    Call<PredictResponse> predict(@Body PredictRequest request);

    @GET("history")
    Call<List<HistoryItem>> getHistory(@Query("machine_id") String machineId);
}
