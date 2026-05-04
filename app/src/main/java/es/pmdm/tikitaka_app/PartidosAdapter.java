package es.pmdm.tikitaka_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import es.pmdm.tikitaka_app.modelos.Partido;

public class PartidosAdapter extends RecyclerView.Adapter<PartidosAdapter.PartidoViewHolder> {

    private List<Partido> partidos;
    private Context context;
    private SimpleDateFormat timeFormat;

    public PartidosAdapter(List<Partido> partidos, Context context) {
        this.partidos = partidos;
        this.context = context;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public PartidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_partido, parent, false);
        return new PartidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartidoViewHolder holder, int position) {
        Partido partido = partidos.get(position);

        // Equipos
        holder.tvEquipoLocal.setText(partido.getEquipoLocal().getNombre());
        holder.tvEquipoVisitante.setText(partido.getEquipoVisitante().getNombre());

        // Estado del partido
        String estado = "";
        int color = 0;

        switch (partido.getEstado()) {
            case Partido.ESTADO_EN_VIVO:
                estado = context.getString(R.string.live_matches).toUpperCase();
                color = context.getResources().getColor(R.color.match_live);
                holder.tvMinuto.setVisibility(View.VISIBLE);
                holder.tvMinuto.setText(partido.getMinutoActual() + "'");
                holder.tvFechaHora.setVisibility(View.GONE);
                break;
            case Partido.ESTADO_PROGRAMADO:
                estado = context.getString(R.string.upcoming_matches).toUpperCase();
                color = context.getResources().getColor(R.color.match_upcoming);
                holder.tvMinuto.setVisibility(View.GONE);
                holder.tvFechaHora.setVisibility(View.VISIBLE);
                holder.tvFechaHora.setText(partido.getFechaHora());
                break;
            case Partido.ESTADO_FINALIZADO:
                estado = context.getString(R.string.finished_matches).toUpperCase();
                color = context.getResources().getColor(R.color.match_finished);
                holder.tvMinuto.setVisibility(View.GONE);
                holder.tvFechaHora.setVisibility(View.VISIBLE);
                holder.tvFechaHora.setText("FT");
                break;
        }

        holder.tvEstado.setText(estado);
        holder.tvEstado.setTextColor(color);

        // Marcador
        if (Partido.ESTADO_PROGRAMADO.equals(partido.getEstado())) {
            holder.tvGolesLocal.setText("-");
            holder.tvGolesVisitante.setText("-");
        } else {
            holder.tvGolesLocal.setText(String.valueOf(partido.getGolesLocal()));
            holder.tvGolesVisitante.setText(String.valueOf(partido.getGolesVisitante()));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallePartidoActivity.class);
            intent.putExtra("partido_id", partido.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return partidos.size();
    }

    public void updatePartidos(List<Partido> nuevosPartidos) {
        this.partidos = nuevosPartidos;
        notifyDataSetChanged();
    }

    static class PartidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvCompeticion, tvEstado, tvEquipoLocal, tvEquipoVisitante;
        TextView tvGolesLocal, tvGolesVisitante, tvMinuto, tvFechaHora;

        public PartidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompeticion = itemView.findViewById(R.id.tvCompeticion);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvEquipoLocal = itemView.findViewById(R.id.tvEquipoLocal);
            tvEquipoVisitante = itemView.findViewById(R.id.tvEquipoVisitante);
            tvGolesLocal = itemView.findViewById(R.id.tvGolesLocal);
            tvGolesVisitante = itemView.findViewById(R.id.tvGolesVisitante);
            tvMinuto = itemView.findViewById(R.id.tvMinuto);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
        }
    }
}
