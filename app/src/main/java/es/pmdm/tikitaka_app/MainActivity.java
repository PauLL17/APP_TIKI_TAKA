package es.pmdm.tikitaka_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnLive, btnUpcoming, btnFinished;
    private RecyclerView rvPartidos;
    private TextView tvNoMatches;
    private ProgressBar progressBar;

    private PartidosAdapter adapter;
    private List<Partido> todosPartidos;
    private String filtroActual = Partido.ESTADO_EN_VIVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupFilterButtons();
        loadPartidos();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        btnLive = findViewById(R.id.btnLive);
        btnUpcoming = findViewById(R.id.btnUpcoming);
        btnFinished = findViewById(R.id.btnFinished);
        rvPartidos = findViewById(R.id.rvPartidos);
        tvNoMatches = findViewById(R.id.tvNoMatches);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.results_title);
        }
    }

    private void setupRecyclerView() {
        todosPartidos = new ArrayList<>();
        adapter = new PartidosAdapter(new ArrayList<>(), this);
        rvPartidos.setLayoutManager(new LinearLayoutManager(this));
        rvPartidos.setAdapter(adapter);
    }

    private void setupFilterButtons() {
        btnLive.setOnClickListener(v -> filtrarPartidos(Partido.ESTADO_EN_VIVO));
        btnUpcoming.setOnClickListener(v -> filtrarPartidos(Partido.ESTADO_PROGRAMADO));
        btnFinished.setOnClickListener(v -> filtrarPartidos(Partido.ESTADO_FINALIZADO));
    }

    private void loadPartidos() {
        // Mostrar loading
        progressBar.setVisibility(View.VISIBLE);
        rvPartidos.setVisibility(View.GONE);
        tvNoMatches.setVisibility(View.GONE);

        // Por ahora, datos de prueba
        todosPartidos = getDatosPrueba();

        // Filtrar por el estado actual
        filtrarPartidos(filtroActual);

        // Ocultar loading
        progressBar.setVisibility(View.GONE);
    }

    private void filtrarPartidos(String estado) {
        filtroActual = estado;

        List<Partido> partidosFiltrados = new ArrayList<>();
        for (Partido partido : todosPartidos) {
            if (partido.getEstado().equals(estado)) {
                partidosFiltrados.add(partido);
            }
        }

        if (partidosFiltrados.isEmpty()) {
            tvNoMatches.setVisibility(View.VISIBLE);
            rvPartidos.setVisibility(View.GONE);
        } else {
            tvNoMatches.setVisibility(View.GONE);
            rvPartidos.setVisibility(View.VISIBLE);
            adapter.updatePartidos(partidosFiltrados);
        }
    }

    private List<Partido> getDatosPrueba() {
        List<Partido> partidos = new ArrayList<>();

        Equipo realMadrid = new Equipo();
        realMadrid.setId(1L);
        realMadrid.setNombre("Real Madrid");

        Equipo barcelona = new Equipo();
        barcelona.setId(2L);
        barcelona.setNombre("FC Barcelona");

        Equipo atletico = new Equipo();
        atletico.setId(3L);
        atletico.setNombre("Atlético Madrid");

        Equipo valencia = new Equipo();
        valencia.setId(4L);
        valencia.setNombre("Valencia CF");

        Partido p1 = new Partido();
        p1.setId(1L);
        p1.setEquipoLocal(realMadrid);
        p1.setEquipoVisitante(barcelona);
        p1.setGolesLocal(1);
        p1.setGolesVisitante(1);
        p1.setEstado(Partido.ESTADO_EN_VIVO);
        p1.setMinutoActual(75);
        p1.setFechaHora("2025-05-15T20:30:00");
        partidos.add(p1);

        Partido p2 = new Partido();
        p2.setId(2L);
        p2.setEquipoLocal(atletico);
        p2.setEquipoVisitante(valencia);
        p2.setGolesLocal(0);
        p2.setGolesVisitante(0);
        p2.setEstado(Partido.ESTADO_EN_VIVO);
        p2.setMinutoActual(30);
        p2.setFechaHora("2025-05-15T18:00:00");
        partidos.add(p2);

        Partido p3 = new Partido();
        p3.setId(3L);
        p3.setEquipoLocal(realMadrid);
        p3.setEquipoVisitante(atletico);
        p3.setEstado(Partido.ESTADO_PROGRAMADO);
        p3.setFechaHora("2025-05-20T21:00:00");
        partidos.add(p3);

        Partido p4 = new Partido();
        p4.setId(4L);
        p4.setEquipoLocal(barcelona);
        p4.setEquipoVisitante(valencia);
        p4.setGolesLocal(3);
        p4.setGolesVisitante(0);
        p4.setEstado(Partido.ESTADO_FINALIZADO);
        p4.setFechaHora("2025-05-14T20:00:00");
        partidos.add(p4);

        return partidos;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d("MainActivity", "Menú creado con " + menu.size() + " items");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Log.d("MainActivity", "Item pulsado: " + item.getTitle());

        if (itemId == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SessionManager.cerrarSesion(this);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}