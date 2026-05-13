package es.pmdm.tikitaka_app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.pmdm.tikitaka_app.adapters.RankingAdapter;
import es.pmdm.tikitaka_app.api.API;
import es.pmdm.tikitaka_app.api.UtilJSONParser;
import es.pmdm.tikitaka_app.api.UtilREST;
import es.pmdm.tikitaka_app.modelos.RankingItem;

public class EstadisticasActivity extends AppCompatActivity {

    private ListView lvGoleadores, lvMasTarjetas, lvPartidosMasGoles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        initViews();
        setupToolbar();
        cargarGoleadores();
        cargarMasTarjetas();
        cargarPartidosMasGoles();
    }

    private void initViews() {
        lvGoleadores      = findViewById(R.id.lvGoleadores);
        lvMasTarjetas     = findViewById(R.id.lvMasTarjetas);
        lvPartidosMasGoles = findViewById(R.id.lvPartidosMasGoles);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.estadisticas_title);
        }
    }

    private void cargarGoleadores() {
        String token = SessionManager.getToken(this);

        API.getGoleadores(token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                List<RankingItem> goleadores = UtilJSONParser.parseGoleadores(r.content);
                lvGoleadores.setAdapter(new RankingAdapter(EstadisticasActivity.this, goleadores));
            }

            @Override
            public void onError(UtilREST.Response r) {
                lvGoleadores.setAdapter(new RankingAdapter(EstadisticasActivity.this, new ArrayList<>()));
            }
        });
    }

    private void cargarMasTarjetas() {
        String token = SessionManager.getToken(this);

        API.getMasTarjetas(token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                List<RankingItem> masTarjetas = UtilJSONParser.parseMasTarjetas(r.content);
                lvMasTarjetas.setAdapter(new RankingAdapter(EstadisticasActivity.this, masTarjetas));
            }

            @Override
            public void onError(UtilREST.Response r) {
                lvMasTarjetas.setAdapter(new RankingAdapter(EstadisticasActivity.this, new ArrayList<>()));
            }
        });
    }

    private void cargarPartidosMasGoles() {
        String token = SessionManager.getToken(this);

        API.getPartidosMasGolesJooq(token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                List<RankingItem> partidos = new ArrayList<>();
                try {
                    JSONArray array = new JSONArray(r.content);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = array.getJSONObject(i);
                        long golesLocal     = json.optLong("golesLocal", 0);
                        long golesVisitante = json.optLong("golesVisitante", 0);
                        int total           = (int) json.optLong("total", 0);
                        String nombreLocal     = json.optString("nombreEquipoLocal", "Local");
                        String nombreVisitante = json.optString("nombreEquipoVisitante", "Visitante");
                        String nombre = nombreLocal + " " + golesLocal + "-" + golesVisitante + " " + nombreVisitante;
                        partidos.add(new RankingItem(nombre, total));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                lvPartidosMasGoles.setAdapter(new RankingAdapter(EstadisticasActivity.this, partidos));
            }

            @Override
            public void onError(UtilREST.Response r) {
                lvPartidosMasGoles.setAdapter(new RankingAdapter(EstadisticasActivity.this, new ArrayList<>()));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}