package com.savemachine.app.ui.predict;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.savemachine.app.R;
import com.savemachine.app.model.PredictRequest;
import com.savemachine.app.model.PredictResponse;
import com.savemachine.app.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictActivity extends AppCompatActivity {

    private TextInputEditText etTempAr, etTempProc, etRpm, etTorque, etDesgaste;
    private Button btnPredict;
    private ProgressBar progressBar;
    private CardView cardResult;
    private TextView tvResultStatus, tvResultProb, tvResultMsg, tvResultDate;
    private LinearLayout layoutResult;

    private String machineId, machineModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);

        machineId    = getIntent().getStringExtra("machine_id");
        machineModel = getIntent().getStringExtra("machine_model");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(machineId);
        getSupportActionBar().setSubtitle(machineModel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTempAr    = findViewById(R.id.etTempAr);
        etTempProc  = findViewById(R.id.etTempProc);
        etRpm       = findViewById(R.id.etRpm);
        etTorque    = findViewById(R.id.etTorque);
        etDesgaste  = findViewById(R.id.etDesgaste);
        btnPredict  = findViewById(R.id.btnPredict);
        progressBar = findViewById(R.id.progressBar);
        cardResult  = findViewById(R.id.cardResult);
        layoutResult    = findViewById(R.id.layoutResult);
        tvResultStatus  = findViewById(R.id.tvResultStatus);
        tvResultProb    = findViewById(R.id.tvResultProb);
        tvResultMsg     = findViewById(R.id.tvResultMsg);
        tvResultDate    = findViewById(R.id.tvResultDate);

        // Preenche com valores típicos de referência
        etTempAr.setHint("Ex: 300.5 K");
        etTempProc.setHint("Ex: 310.8 K");
        etRpm.setHint("Ex: 1500");
        etTorque.setHint("Ex: 40.5 Nm");
        etDesgaste.setHint("Ex: 120 min");

        btnPredict.setOnClickListener(v -> doPredict());
    }

    private void doPredict() {
        String sTempAr   = etTempAr.getText().toString().trim();
        String sTempProc = etTempProc.getText().toString().trim();
        String sRpm      = etRpm.getText().toString().trim();
        String sTorque   = etTorque.getText().toString().trim();
        String sDesgaste = etDesgaste.getText().toString().trim();

        if (sTempAr.isEmpty() || sTempProc.isEmpty() || sRpm.isEmpty()
                || sTorque.isEmpty() || sDesgaste.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double tAr, tProc, rpm, torque, desgaste;
        try {
            tAr     = Double.parseDouble(sTempAr);
            tProc   = Double.parseDouble(sTempProc);
            rpm     = Double.parseDouble(sRpm);
            torque  = Double.parseDouble(sTorque);
            desgaste = Double.parseDouble(sDesgaste);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Use apenas números nos campos", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        cardResult.setVisibility(View.GONE);

        PredictRequest req = new PredictRequest(machineId, tAr, tProc, rpm, torque, desgaste);
        ApiClient.getService().predict(req).enqueue(new Callback<PredictResponse>() {
            @Override
            public void onResponse(Call<PredictResponse> call, Response<PredictResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    showResult(response.body());
                } else {
                    Toast.makeText(PredictActivity.this, "Erro ao processar predição", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<PredictResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(PredictActivity.this, "Erro de conexão com o servidor", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showResult(PredictResponse r) {
        cardResult.setVisibility(View.VISIBLE);

        tvResultStatus.setText(r.status);
        tvResultProb.setText(String.format("%.1f%% de probabilidade de falha", r.probabilidade));
        tvResultMsg.setText(r.mensagem);
        tvResultDate.setText("Próxima manutenção: " + r.proximaManutencao);

        int color;
        switch (r.status) {
            case "Risco Crítico": color = Color.parseColor("#EF4444"); break;
            case "Risco Alto":    color = Color.parseColor("#F59E0B"); break;
            case "Risco Moderado":color = Color.parseColor("#EAB308"); break;
            default:              color = Color.parseColor("#10B981"); break;
        }
        tvResultStatus.setTextColor(color);
        cardResult.setCardBackgroundColor(Color.parseColor("#F8FAFC"));
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnPredict.setEnabled(!loading);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
