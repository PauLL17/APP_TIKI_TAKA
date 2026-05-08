package es.pmdm.tikitaka_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import es.pmdm.tikitaka_app.R;

public class EventoAdapter extends ArrayAdapter<String> {

    public EventoAdapter(Context context, List<String> eventos) {
        super(context, R.layout.item_evento, eventos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_evento, parent, false);
        }

        String evento = getItem(position);
        // Formato: "45' ⚽ Benzema" o "67' 🟡 Casemiro"
        String[] partes = evento.split(" ", 3);

        TextView tvMinuto = convertView.findViewById(R.id.tvMinutoEvento);
        TextView tvIcono  = convertView.findViewById(R.id.tvIconoEvento);
        TextView tvNombre = convertView.findViewById(R.id.tvNombreEvento);

        if (partes.length == 3) {
            tvMinuto.setText(partes[0]);
            tvIcono.setText(partes[1]);
            tvNombre.setText(partes[2]);
        } else {
            tvMinuto.setText("");
            tvIcono.setText("");
            tvNombre.setText(evento);
        }

        return convertView;
    }
}