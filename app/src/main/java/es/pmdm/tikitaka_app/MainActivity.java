package es.pmdm.tikitaka_app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.pmdm.tikitaka_app.adapters.PartidosAdapter;
import es.pmdm.tikitaka_app.api.API;
import es.pmdm.tikitaka_app.api.UtilJSONParser;
import es.pmdm.tikitaka_app.api.UtilREST;
import es.pmdm.tikitaka_app.modelos.Equipo;
import es.pmdm.tikitaka_app.modelos.Partido;

public class MainActivity extends BaseActivity {
    private static final int CODIGO_PERMISO_NOTIFICACIONES = 100;

    private DrawerLayout drawer;
    private NavigationView navView;
    private Toolbar toolbar;
    private Button btnLive, btnUpcoming, btnFinished;
    private RecyclerView rvPartidos;
    private TextView tvNoMatches;
    private ProgressBar progressBar;

    private PartidosAdapter adapter;
    private List<Partido> todosPartidos = new ArrayList<>();
    private Map<Long, Equipo> equiposCache = new HashMap<>();
    private String filtroActual = Partido.ESTADO_EN_VIVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupToolbar();
        setupDrawer();
        conectarWebSocket();
        setupRecyclerView();
        setupFilterButtons();
        cargarEquiposYPartidos();
        pedirPermisoNotificaciones();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarEquiposYPartidos();
    }

    private void initViews() {
        drawer = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
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

    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_equipos) {
                startActivity(new Intent(this, EquiposActivity.class));
            } else if (id == R.id.nav_noticias) {
                startActivity(new Intent(this, NoticiasActivity.class));
            } else if (id == R.id.nav_estadisticas) {
                startActivity(new Intent(this, EstadisticasActivity.class));
            } else if (id == R.id.nav_busqueda_jugadores) {
                startActivity(new Intent(this, BusquedaJugadoresActivity.class));
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
            } else if (id == R.id.nav_ajustes) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
            } else if (id == R.id.nav_logout) {
                logout();
            }

            drawer.closeDrawers();
            return true;
        });
    }

    private void setupRecyclerView() {
        adapter = new PartidosAdapter(new ArrayList<>(), this);
        rvPartidos.setLayoutManager(new LinearLayoutManager(this));
        rvPartidos.setAdapter(adapter);
    }

    private void setupFilterButtons() {
        btnLive.setOnClickListener(v -> filtrarPartidos(Partido.ESTADO_EN_VIVO));
        btnUpcoming.setOnClickListener(v -> filtrarPartidos(Partido.ESTADO_PROGRAMADO));
        btnFinished.setOnClickListener(v -> filtrarPartidos(Partido.ESTADO_FINALIZADO));
    }

    private void cargarEquiposYPartidos() {
        progressBar.setVisibility(View.VISIBLE);
        rvPartidos.setVisibility(View.GONE);
        tvNoMatches.setVisibility(View.GONE);

        String token = SessionManager.getToken(this);

        API.getEquipos(token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                List<Equipo> equipos = UtilJSONParser.parseArrayEquipos(r.content);
                for (Equipo equipo : equipos) {
                    equiposCache.put(equipo.getId(), equipo);
                }
                cargarPartidos();
            }

            @Override
            public void onError(UtilREST.Response r) {
                progressBar.setVisibility(View.GONE);
                ToastPersonalizado.mostrarError(MainActivity.this, getString(R.string.error_cargar_datos));
            }
        });
    }

    private void cargarPartidos() {
        String token = SessionManager.getToken(this);

        API.getPartidos(token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                todosPartidos = UtilJSONParser.parseArrayPartidos(r.content);

                for (Partido partido : todosPartidos) {
                    partido.setEquipoLocal(equiposCache.get(partido.getEquipoLocalId()));
                    partido.setEquipoVisitante(equiposCache.get(partido.getEquipoVisitanteId()));
                }

                progressBar.setVisibility(View.GONE);
                filtrarPartidos(filtroActual);
            }

            @Override
            public void onError(UtilREST.Response r) {
                progressBar.setVisibility(View.GONE);
                ToastPersonalizado.mostrarError(MainActivity.this, getString(R.string.error_cargar_datos));
            }
        });
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

    private void logout() {
        WebSocketManager.getInstance().desconectarTodo();
        SessionManager.cerrarSesion(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        CODIGO_PERMISO_NOTIFICACIONES);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PERMISO_NOTIFICACIONES) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ToastPersonalizado.mostrarError(this,
                        getString(R.string.permiso_notificaciones_denegado));
            }
        }
    }

    private void conectarWebSocket() {
            long usuarioId = SessionManager.getUsuarioId(this);
            WebSocketManager.getInstance().conectarNotificaciones(this, usuarioId);
    }
}