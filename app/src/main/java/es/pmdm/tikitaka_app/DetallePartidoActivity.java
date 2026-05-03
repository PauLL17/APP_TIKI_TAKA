package es.pmdm.tikitaka_app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import java.util.Date;

public class DetallePartidoActivity extends AppCompatActivity {

    private TextView tvCompeticion, tvEquipoLocal, tvEquipoVisitante;
    private TextView tvGolesLocal, tvGolesVisitante, tvMinuto;
    private TextView tvPosesionLocal, tvPosesionVisitante;
    private TextView tvTirosPuertaLocal, tvTirosPuertaVisitante;
    private TextView tvTarjetasAmarillasLocal, tvTarjetasAmarillasVisitante;
    private TextView tvTarjetasRojasLocal, tvTarjetasRojasVisitante;

    private Partido partido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_partido);

        setupToolbar();
        initViews();
        loadPartidoData();
        displayPartidoInfo();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.match_details);
        }
    }

    private void initViews() {
        tvCompeticion = findViewById(R.id.tvCompeticion);
        tvEquipoLocal = findViewById(R.id.tvEquipoLocal);
        tvEquipoVisitante = findViewById(R.id.tvEquipoVisitante);
        tvGolesLocal = findViewById(R.id.tvGolesLocal);
        tvGolesVisitante = findViewById(R.id.tvGolesVisitante);
        tvMinuto = findViewById(R.id.tvMinuto);
        tvPosesionLocal = findViewById(R.id.tvPosesionLocal);
        tvPosesionVisitante = findViewById(R.id.tvPosesionVisitante);
        tvTirosPuertaLocal = findViewById(R.id.tvTirosPuertaLocal);
        tvTirosPuertaVisitante = findViewById(R.id.tvTirosPuertaVisitante);
        tvTarjetasAmarillasLocal = findViewById(R.id.tvTarjetasAmarillasLocal);
        tvTarjetasAmarillasVisitante = findViewById(R.id.tvTarjetasAmarillasVisitante);
        tvTarjetasRojasLocal = findViewById(R.id.tvTarjetasRojasLocal);
        tvTarjetasRojasVisitante = findViewById(R.id.tvTarjetasRojasVisitante);
    }

    private void loadPartidoData() {
        int partidoId = getIntent().getIntExtra("partido_id", 0);

        //datos de prueba
        Equipo realMadrid = new Equipo(1, "Real Madrid", "Madrid", "Santiago Bernabéu");
        Equipo barcelona = new Equipo(2, "FC Barcelona", "Barcelona", "Camp Nou");

        partido = new Partido(1, realMadrid, barcelona, 1, 1, new Date(), "en_vivo", "LaLiga");
        partido.setMinutoActual(75);
        partido.setPosesionLocal(45);
        partido.setPosesionVisitante(55);
        partido.setTirosAPuertaLocal(6);
        partido.setTirosAPuertaVisitante(8);
        partido.setTarjetasAmarillasLocal(2);
        partido.setTarjetasAmarillasVisitante(3);
    }

    private void displayPartidoInfo() {
        // Competición
        tvCompeticion.setText(partido.getCompeticion());

        // Equipos
        tvEquipoLocal.setText(partido.getEquipoLocal().getNombre());
        tvEquipoVisitante.setText(partido.getEquipoVisitante().getNombre());

        // Marcador
        tvGolesLocal.setText(String.valueOf(partido.getGolesLocal()));
        tvGolesVisitante.setText(String.valueOf(partido.getGolesVisitante()));

        // Minuto
        if (partido.isEnVivo()) {
            tvMinuto.setText(partido.getMinutoActual() + "'");
        } else if (partido.isFinalizado()) {
            tvMinuto.setText("FT");
        } else {
            tvMinuto.setText("--:--");
        }

        // Estadísticas
        tvPosesionLocal.setText(partido.getPosesionLocal() + "%");
        tvPosesionVisitante.setText(partido.getPosesionVisitante() + "%");

        tvTirosPuertaLocal.setText(String.valueOf(partido.getTirosAPuertaLocal()));
        tvTirosPuertaVisitante.setText(String.valueOf(partido.getTirosAPuertaVisitante()));

        tvTarjetasAmarillasLocal.setText(String.valueOf(partido.getTarjetasAmarillasLocal()));
        tvTarjetasAmarillasVisitante.setText(String.valueOf(partido.getTarjetasAmarillasVisitante()));

        tvTarjetasRojasLocal.setText("0");
        tvTarjetasRojasVisitante.setText("0");
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