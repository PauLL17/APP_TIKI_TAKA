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
    private String filtroActual = "en_vivo";

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
        btnLive.setOnClickListener(v -> filtrarPartidos("en_vivo"));
        btnUpcoming.setOnClickListener(v -> filtrarPartidos("programado"));
        btnFinished.setOnClickListener(v -> filtrarPartidos("finalizado"));
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

        // Crear equipos de prueba
        Equipo realMadrid = new Equipo(1, "Real Madrid", "Madrid", "Santiago Bernabéu");
        Equipo barcelona = new Equipo(2, "FC Barcelona", "Barcelona", "Camp Nou");
        Equipo atletico = new Equipo(3, "Atlético Madrid", "Madrid", "Metropolitano");
        Equipo valencia = new Equipo(4, "Valencia CF", "Valencia", "Mestalla");
        Equipo sevilla = new Equipo(5, "Sevilla FC", "Sevilla", "Sánchez-Pizjuán");
        Equipo realSociedad = new Equipo(6, "Real Sociedad", "San Sebastián", "Reale Arena");

        // Partidos EN VIVO
        Partido p1 = new Partido(1, realMadrid, barcelona, 1, 1, new Date(), "en_vivo", "LaLiga");
        p1.setMinutoActual(75);
        p1.setPosesionLocal(45);
        p1.setPosesionVisitante(55);
        p1.setTirosAPuertaLocal(6);
        p1.setTirosAPuertaVisitante(8);
        p1.setTarjetasAmarillasLocal(2);
        p1.setTarjetasAmarillasVisitante(3);
        partidos.add(p1);

        Partido p2 = new Partido(2, atletico, valencia, 0, 0, new Date(), "en_vivo", "LaLiga");
        p2.setMinutoActual(30);
        partidos.add(p2);

        // Partidos PRÓXIMOS
        Partido p3 = new Partido(3, sevilla, realSociedad, 0, 0, new Date(), "programado", "LaLiga");
        partidos.add(p3);

        Partido p4 = new Partido(4, barcelona, atletico, 0, 0, new Date(), "programado", "Copa del Rey");
        partidos.add(p4);

        // Partidos FINALIZADOS
        Partido p5 = new Partido(5, realMadrid, atletico, 2, 1, new Date(), "finalizado", "LaLiga");
        p5.setMinutoActual(90);
        partidos.add(p5);

        Partido p6 = new Partido(6, barcelona, valencia, 3, 0, new Date(), "finalizado", "LaLiga");
        p6.setMinutoActual(90);
        partidos.add(p6);

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
        SharedPreferences sharedPreferences = getSharedPreferences("TikiTakaPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}