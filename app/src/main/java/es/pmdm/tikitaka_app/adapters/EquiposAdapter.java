package es.pmdm.tikitaka_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import es.pmdm.tikitaka_app.R;
import es.pmdm.tikitaka_app.modelos.Equipo;

public class EquiposAdapter extends ArrayAdapter<Equipo> {
    public EquiposAdapter(Context context, List<Equipo> equipos) {
        super(context, R.layout.item_equipo, equipos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_equipo, parent, false);
        }

        Equipo equipo = getItem(position);

        TextView tvNombre  = convertView.findViewById(R.id.tvNombreEquipo);
        TextView tvCiudad  = convertView.findViewById(R.id.tvCiudadEquipo);
        TextView tvEstadio = convertView.findViewById(R.id.tvEstadioEquipo);

        tvNombre.setText(equipo.getNombre());
        tvCiudad.setText(equipo.getCiudad());
        tvEstadio.setText(equipo.getEstadio());

        return convertView;
    }
}