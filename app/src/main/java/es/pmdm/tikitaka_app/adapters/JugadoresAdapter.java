package es.pmdm.tikitaka_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import es.pmdm.tikitaka_app.R;
import es.pmdm.tikitaka_app.modelos.Jugador;

public class JugadoresAdapter extends ArrayAdapter<Jugador>
{
    public JugadoresAdapter(Context context, List<Jugador> jugadores) {
        super(context, R.layout.item_titular, jugadores);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_titular, parent, false);
        }

        Jugador jugador = getItem(position);

        TextView tvDorsal = convertView.findViewById(R.id.tvDorsal);
        TextView tvNombre = convertView.findViewById(R.id.tvNombreTitular);

        tvDorsal.setText(String.valueOf(jugador.getDorsal()));
        tvNombre.setText(jugador.getNombreCompleto());

        return convertView;
    }
}
