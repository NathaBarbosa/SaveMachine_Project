package com.savemachine.app.model;

import com.google.gson.annotations.SerializedName;

public class Machine {
    @SerializedName("machine_id")       public String machineId;
    @SerializedName("model")            public String model;
    @SerializedName("status")           public String status;
    @SerializedName("risk")             public double risk;
    @SerializedName("next_maintenance") public String nextMaintenance;
    @SerializedName("created_at")       public String createdAt;
}
