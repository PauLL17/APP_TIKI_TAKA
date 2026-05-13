package es.pmdm.tikitaka_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import es.pmdm.tikitaka_app.R;
import es.pmdm.tikitaka_app.modelos.RankingItem;

public class RankingAdapter extends ArrayAdapter<RankingItem>
{
    public RankingAdapter(Context context, List<RankingItem> items) {
        super(context, R.layout.item_ranking, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_ranking, parent, false);
        }

        RankingItem item = getItem(position);

        TextView tvPosicion = convertView.findViewById(R.id.tvPosicion);
        TextView tvNombre   = convertView.findViewById(R.id.tvNombreRanking);
        TextView tvTotal    = convertView.findViewById(R.id.tvTotalRanking);

        tvPosicion.setText(String.valueOf(position + 1));
        tvNombre.setText(item.getNombre());
        tvTotal.setText(String.valueOf(item.getTotal()));

        return convertView;
    }
}
