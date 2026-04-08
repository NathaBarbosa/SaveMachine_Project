package com.savemachine.app.ui.history;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.savemachine.app.R;
import com.savemachine.app.model.HistoryItem;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryItem> items;

    public HistoryAdapter(List<HistoryItem> items) { this.items = items; }

    public void updateList(List<HistoryItem> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        HistoryItem item = items.get(position);

        h.tvStatus.setText(item.status);
        h.tvProb.setText(String.format("%.1f%%", item.probabilidade));
        h.tvDate.setText(formatTimestamp(item.timestamp));
        h.tvMaintenance.setText("Manutenção: " + item.nextMaintenance);
        h.tvSensors.setText(String.format(
                "T.Ar: %.1fK  |  T.Proc: %.1fK  |  RPM: %.0f  |  Torque: %.1f Nm  |  Desgaste: %.0f min",
                item.temperaturaAr, item.temperaturaProcesso,
                item.rotacaoRpm, item.torque, item.desgasteFerramenta));

        int color;
        switch (item.status) {
            case "Risco Crítico": color = Color.parseColor("#EF4444"); break;
            case "Risco Alto":    color = Color.parseColor("#F59E0B"); break;
            case "Risco Moderado":color = Color.parseColor("#EAB308"); break;
            default:              color = Color.parseColor("#10B981"); break;
        }
        h.tvStatus.setTextColor(color);
        h.tvProb.setTextColor(color);
    }

    private String formatTimestamp(String ts) {
        if (ts == null || ts.length() < 16) return ts;
        // "2024-01-15T14:30:00" → "15/01/2024 14:30"
        try {
            String[] parts = ts.split("T");
            String[] date  = parts[0].split("-");
            return date[2] + "/" + date[1] + "/" + date[0] + "  " + parts[1].substring(0, 5);
        } catch (Exception e) { return ts; }
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatus, tvProb, tvDate, tvMaintenance, tvSensors;
        ViewHolder(View v) {
            super(v);
            tvStatus      = v.findViewById(R.id.tvStatus);
            tvProb        = v.findViewById(R.id.tvProb);
            tvDate        = v.findViewById(R.id.tvDate);
            tvMaintenance = v.findViewById(R.id.tvMaintenance);
            tvSensors     = v.findViewById(R.id.tvSensors);
        }
    }
}
