package es.pmdm.tikitaka_app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        conectarWebSocket();
        setupRecyclerView();
        setupFilterButtons();
        cargarEquiposYPartidos();
        pedirPermisoNotificaciones();
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
        adapter = new PartidosAdapter(new ArrayList<>(), this);
        rvPartidos.setLayoutManager(new LinearLayoutManager(this));
        rvPartidos.setAdapter(adapter);
    }

    private void setupFilterButtons() {
        btnLive.setOnClickListener(v -> filtrarPartidos(Partido.ESTADO_EN_VIVO));
        btnUpcoming.setOnClickListener(v -> filtrarPartidos(Partido.ESTADO_PROGRAMADO));
        btnFinished.setOnClickListener(v -> filtrarPartidos(Partido.ESTADO_FINALIZADO));
    }

    // Primero cargamos los equipos, luego los partidos
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

                // Resolver equipos desde la caché
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }

        if (item.getItemId() == R.id.action_equipos) {
            Intent intent = new Intent(this, EquiposActivity.class);
            startActivity(intent);
            return true;
        }

        if (item.getItemId() == R.id.action_noticias) {
            Intent intent = new Intent(this, NoticiasActivity.class);
            startActivity(intent);
            return true;
        }

        if (item.getItemId() == R.id.action_estadisticas) {
            Intent intent = new Intent(this, EstadisticasActivity.class);
            startActivity(intent);
            return true;
        }

        if (item.getItemId() == R.id.action_perfil) {
            Intent intent = new Intent(this, PerfilActivity.class);
            startActivity(intent);
            return true;
        }

        if (item.getItemId() == R.id.action_ajustes) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        if (!SessionManager.esInvitado(this)) {
            long usuarioId = SessionManager.getUsuarioId(this);
            WebSocketManager.getInstance().conectarNotificaciones(this, usuarioId);
        }
    }
}