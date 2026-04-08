package com.savemachine.app.ui.machines;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.savemachine.app.R;
import com.savemachine.app.model.AddMachineRequest;
import com.savemachine.app.model.Machine;
import com.savemachine.app.model.MessageResponse;
import com.savemachine.app.network.ApiClient;
import com.savemachine.app.ui.login.LoginActivity;
import com.savemachine.app.ui.predict.PredictActivity;
import com.savemachine.app.ui.history.HistoryActivity;
import com.savemachine.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MachinesActivity extends AppCompatActivity implements MachineAdapter.MachineListener {

    private RecyclerView recyclerView;
    private MachineAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private SwipeRefreshLayout swipeRefresh;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machines);

        session = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SaveMachine");
        getSupportActionBar().setSubtitle("Olá, " + session.getName());

        recyclerView  = findViewById(R.id.recyclerMachines);
        progressBar   = findViewById(R.id.progressBar);
        tvEmpty       = findViewById(R.id.tvEmpty);
        swipeRefresh  = findViewById(R.id.swipeRefresh);

        adapter = new MachineAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> showAddMachineDialog());

        swipeRefresh.setOnRefreshListener(this::loadMachines);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        loadMachines();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMachines();
    }

    private void loadMachines() {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getService().getMachines()
                .enqueue(new Callback<List<Machine>>() {
                    @Override
                    public void onResponse(Call<List<Machine>> call, Response<List<Machine>> response) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefresh.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null) {
                            List<Machine> machines = response.body();
                            adapter.updateList(machines);
                            tvEmpty.setVisibility(machines.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Machine>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(MachinesActivity.this, "Erro ao carregar máquinas", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddMachineDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_machine, null);
        TextInputEditText etId    = dialogView.findViewById(R.id.etMachineId);
        TextInputEditText etModel = dialogView.findViewById(R.id.etMachineModel);

        new AlertDialog.Builder(this)
                .setTitle("Adicionar Máquina")
                .setView(dialogView)
                .setPositiveButton("Adicionar", (d, w) -> {
                    String id    = etId.getText().toString().trim();
                    String model = etModel.getText().toString().trim();
                    if (!id.isEmpty() && !model.isEmpty()) addMachine(id, model);
                    else Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void addMachine(String id, String model) {
        ApiClient.getService().addMachine(new AddMachineRequest(id, model))
                .enqueue(new Callback<Machine>() {
                    @Override
                    public void onResponse(Call<Machine> call, Response<Machine> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MachinesActivity.this, "Máquina adicionada!", Toast.LENGTH_SHORT).show();
                            loadMachines();
                        } else {
                            Toast.makeText(MachinesActivity.this, "ID já cadastrado", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Machine> call, Throwable t) {
                        Toast.makeText(MachinesActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ── MachineAdapter.MachineListener ────────────────────────────────────
    @Override
    public void onPredict(Machine machine) {
        Intent intent = new Intent(this, PredictActivity.class);
        intent.putExtra("machine_id", machine.machineId);
        intent.putExtra("machine_model", machine.model);
        startActivity(intent);
    }

    @Override
    public void onHistory(Machine machine) {
        Intent intent = new Intent(this, HistoryActivity.class);
        intent.putExtra("machine_id", machine.machineId);
        startActivity(intent);
    }

    @Override
    public void onDelete(Machine machine) {
        new AlertDialog.Builder(this)
                .setTitle("Remover Máquina")
                .setMessage("Remover " + machine.machineId + "?")
                .setPositiveButton("Remover", (d, w) -> {
                    ApiClient.getService().deleteMachine(machine.machineId)
                            .enqueue(new Callback<MessageResponse>() {
                                @Override
                                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                                    loadMachines();
                                }
                                @Override
                                public void onFailure(Call<MessageResponse> call, Throwable t) {}
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ── Menu ──────────────────────────────────────────────────────────────
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_machines, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            session.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
