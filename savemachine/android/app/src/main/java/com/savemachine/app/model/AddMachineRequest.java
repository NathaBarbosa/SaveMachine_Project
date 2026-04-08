package com.savemachine.app.model;
import com.google.gson.annotations.SerializedName;
public class AddMachineRequest {
    @SerializedName("machine_id") public String machineId;
    @SerializedName("model")      public String model;
    public AddMachineRequest(String machineId, String model) {
        this.machineId = machineId;
        this.model = model;
    }
}
