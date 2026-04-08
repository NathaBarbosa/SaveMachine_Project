package com.savemachine.app.model;
import com.google.gson.annotations.SerializedName;
public class PredictResponse {
    @SerializedName("machine_id")        public String machineId;
    @SerializedName("probabilidade")     public double probabilidade;
    @SerializedName("status")            public String status;
    @SerializedName("mensagem")          public String mensagem;
    @SerializedName("proxima_manutencao") public String proximaManutencao;
    @SerializedName("error")             public String error;
}
