package com.savemachine.app.model;
import com.google.gson.annotations.SerializedName;
public class HistoryItem {
    @SerializedName("id")                   public int id;
    @SerializedName("machine_id")           public String machineId;
    @SerializedName("probabilidade")        public double probabilidade;
    @SerializedName("status")               public String status;
    @SerializedName("next_maintenance")     public String nextMaintenance;
    @SerializedName("timestamp")            public String timestamp;
    @SerializedName("temperatura_ar")       public double temperaturaAr;
    @SerializedName("temperatura_processo") public double temperaturaProcesso;
    @SerializedName("rotacao_rpm")          public double rotacaoRpm;
    @SerializedName("torque")               public double torque;
    @SerializedName("desgaste_ferramenta")  public double desgasteFerramenta;
}
