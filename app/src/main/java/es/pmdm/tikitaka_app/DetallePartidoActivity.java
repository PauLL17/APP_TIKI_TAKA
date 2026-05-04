package es.pmdm.tikitaka_app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import es.pmdm.tikitaka_app.modelos.Equipo;
import es.pmdm.tikitaka_app.modelos.Partido;

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
        Equipo realMadrid = new Equipo();
        realMadrid.setId(1L);
        realMadrid.setNombre("Real Madrid");

        Equipo barcelona = new Equipo();
        barcelona.setId(2L);
        barcelona.setNombre("FC Barcelona");

        partido = new Partido();
        partido.setId(1L);
        partido.setEquipoLocal(realMadrid);
        partido.setEquipoVisitante(barcelona);
        partido.setGolesLocal(1);
        partido.setGolesVisitante(1);
        partido.setEstado(Partido.ESTADO_EN_VIVO);
        partido.setMinutoActual(75);
        partido.setFechaHora("2025-05-15T20:30:00");
    }

    private void displayPartidoInfo() {
        tvEquipoLocal.setText(partido.getEquipoLocal().getNombre());
        tvEquipoVisitante.setText(partido.getEquipoVisitante().getNombre());

        tvGolesLocal.setText(String.valueOf(partido.getGolesLocal()));
        tvGolesVisitante.setText(String.valueOf(partido.getGolesVisitante()));

        if (partido.isEnVivo()) {
            tvMinuto.setText(partido.getMinutoActual() + "'");
        } else if (partido.isFinalizado()) {
            tvMinuto.setText("FT");
        } else {
            tvMinuto.setText("--:--");
        }

        //cargar estadísticas desde Estadistica cuando conecte la API
        tvPosesionLocal.setText("0%");
        tvPosesionVisitante.setText("0%");
        tvTirosPuertaLocal.setText("0");
        tvTirosPuertaVisitante.setText("0");
        tvTarjetasAmarillasLocal.setText("0");
        tvTarjetasAmarillasVisitante.setText("0");
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