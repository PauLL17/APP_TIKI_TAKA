package es.pmdm.tikitaka_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import es.pmdm.tikitaka_app.R;
import es.pmdm.tikitaka_app.modelos.Noticia;

public class NoticiasAdapter extends ArrayAdapter<Noticia> {
    public NoticiasAdapter(Context context, List<Noticia> noticias) {
        super(context, R.layout.item_noticia, noticias);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_noticia, parent, false);
        }

        Noticia noticia = getItem(position);

        TextView tvTitulo = convertView.findViewById(R.id.tvTituloNoticia);
        TextView tvFecha  = convertView.findViewById(R.id.tvFechaNoticia);

        tvTitulo.setText(noticia.getTitulo());
        tvFecha.setText(formatearFecha(noticia.getFechaPublicacion()));


        return convertView;
    }

    private String formatearFecha(String fecha) {
        try {
            java.text.SimpleDateFormat formatoEntrada = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            java.text.SimpleDateFormat formatoSalida = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
            return formatoSalida.format(formatoEntrada.parse(fecha));
        } catch (Exception e) {
            return fecha;
        }
    }
}
