package com.savemachine.app.model;
import com.google.gson.annotations.SerializedName;
public class PredictRequest {
    @SerializedName("machine_id")           public String machineId;
    @SerializedName("temperatura_ar")       public double temperaturaAr;
    @SerializedName("temperatura_processo") public double temperaturaProcesso;
    @SerializedName("rotacao_rpm")          public double rotacaoRpm;
    @SerializedName("torque")               public double torque;
    @SerializedName("desgaste_ferramenta")  public double desgasteFerramenta;

    public PredictRequest(String machineId, double tAr, double tProc,
                          double rpm, double torque, double desgaste) {
        this.machineId = machineId;
        this.temperaturaAr = tAr;
        this.temperaturaProcesso = tProc;
        this.rotacaoRpm = rpm;
        this.torque = torque;
        this.desgasteFerramenta = desgaste;
    }
}
