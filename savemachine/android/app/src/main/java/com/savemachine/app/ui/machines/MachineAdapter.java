package com.savemachine.app.ui.machines;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.savemachine.app.R;
import com.savemachine.app.model.Machine;

import java.util.List;

public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.ViewHolder> {

    public interface MachineListener {
        void onPredict(Machine machine);
        void onHistory(Machine machine);
        void onDelete(Machine machine);
    }

    private List<Machine> machines;
    private final MachineListener listener;

    public MachineAdapter(List<Machine> machines, MachineListener listener) {
        this.machines = machines;
        this.listener = listener;
    }

    public void updateList(List<Machine> newList) {
        this.machines = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_machine, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Machine m = machines.get(position);

        h.tvId.setText(m.machineId);
        h.tvModel.setText(m.model);
        h.tvRisk.setText(String.format("%.1f%%", m.risk));
        h.tvStatus.setText(m.status);
        h.tvMaintenance.setText(m.nextMaintenance != null ? "Manutenção: " + m.nextMaintenance : "Sem manutenção agendada");

        // Cor do status
        int color;
        switch (m.status) {
            case "Risco Crítico": color = Color.parseColor("#EF4444"); break;
            case "Risco Alto":    color = Color.parseColor("#F59E0B"); break;
            case "Risco Moderado":color = Color.parseColor("#EAB308"); break;
            default:              color = Color.parseColor("#10B981"); break;
        }
        h.tvStatus.setTextColor(color);
        h.tvRisk.setTextColor(color);

        h.btnPredict.setOnClickListener(v -> listener.onPredict(m));
        h.btnHistory.setOnClickListener(v -> listener.onHistory(m));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(m));
    }

    @Override
    public int getItemCount() { return machines.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvModel, tvRisk, tvStatus, tvMaintenance;
        Button btnPredict, btnHistory, btnDelete;

        ViewHolder(View v) {
            super(v);
            tvId          = v.findViewById(R.id.tvMachineId);
            tvModel       = v.findViewById(R.id.tvMachineModel);
            tvRisk        = v.findViewById(R.id.tvRisk);
            tvStatus      = v.findViewById(R.id.tvStatus);
            tvMaintenance = v.findViewById(R.id.tvMaintenance);
            btnPredict    = v.findViewById(R.id.btnPredict);
            btnHistory    = v.findViewById(R.id.btnHistory);
            btnDelete     = v.findViewById(R.id.btnDelete);
        }
    }
}
