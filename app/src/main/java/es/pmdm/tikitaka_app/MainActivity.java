package es.pmdm.tikitaka_app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.pmdm.tikitaka_app.adapters.PartidosAdapter;
import es.pmdm.tikitaka_app.api.API;
import es.pmdm.tikitaka_app.api.UtilJSONParser;
import es.pmdm.tikitaka_app.api.UtilREST;
import es.pmdm.tikitaka_app.modelos.Equipo;
import es.pmdm.tikitaka_app.modelos.Jornada;
import es.pmdm.tikitaka_app.modelos.Partido;

public class MainActivity extends BaseActivity {
    private static final int CODIGO_PERMISO_NOTIFICACIONES = 100;

    private DrawerLayout drawer;
    private NavigationView navView;
    private Toolbar toolbar;
    private Button btnLive, btnUpcoming, btnFinished;
    private Spinner spinnerJornada;
    private RecyclerView rvPartidos;
    private TextView tvNoMatches;
    private ProgressBar progressBar;

    private PartidosAdapter adapter;
    private List<Partido> todosPartidos = new ArrayList<>();
    private Map<Long, Equipo> equiposCache = new HashMap<>();
    private List<Jornada> listaJornadas = new ArrayList<>();
    private String filtroActual = Partido.ESTADO_EN_VIVO;
    private long jornadaFiltro = -1;

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
        cargarJornadas();
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
        spinnerJornada = findViewById(R.id.spinnerJornada);
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

    private void cargarJornadas() {
        String token = SessionManager.getToken(this);

        API.getJornadas(token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                listaJornadas = UtilJSONParser.parseArrayJornadas(r.content);
                setupSpinnerJornadas();
                cargarEquiposYPartidos();
            }

            @Override
            public void onError(UtilREST.Response r) {
                cargarEquiposYPartidos();
            }
        });
    }

    private void setupSpinnerJornadas() {
        List<String> nombresJornadas = new ArrayList<>();
        for (Jornada jornada : listaJornadas) {
            nombresJornadas.add(getString(R.string.jornada_numero, jornada.getNumero()));
        }

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nombresJornadas
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJornada.setAdapter(adapterSpinner);

        spinnerJornada.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jornadaFiltro = listaJornadas.get(position).getId();
                filtrarPartidos(filtroActual);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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

    private void mostrarDialogoCrearPartido() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialogo_partido_nuevo, null);

        Spinner spinnerLocal     = dialogView.findViewById(R.id.spinnerEquipoLocal);
        Spinner spinnerVisitante = dialogView.findViewById(R.id.spinnerEquipoVisitante);
        Spinner spinnerJornada   = dialogView.findViewById(R.id.spinnerJornadaPartido);
        Spinner spinnerEstado    = dialogView.findViewById(R.id.spinnerEstadoPartido);
        EditText etFechaHora     = dialogView.findViewById(R.id.etFechaHoraPartido);

        List<Equipo> equiposList = new ArrayList<>(equiposCache.values());
        List<String> nombresEquipos = new ArrayList<>();
        for (Equipo e : equiposList) {
            nombresEquipos.add(e.getNombre());
        }

        ArrayAdapter<String> adapterEquipos = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombresEquipos);
        adapterEquipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocal.setAdapter(adapterEquipos);
        spinnerVisitante.setAdapter(adapterEquipos);

        List<String> nombresJornadas = new ArrayList<>();
        for (Jornada j : listaJornadas) {
            nombresJornadas.add(getString(R.string.jornada_numero, j.getNumero()));
        }
        ArrayAdapter<String> adapterJornadas = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombresJornadas);
        adapterJornadas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJornada.setAdapter(adapterJornadas);

        String[] estados = {"PROGRAMADO", "EN_VIVO", "FINALIZADO"};
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, estados);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.aniadir_partido))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.dialogo_si), (dialog, which) -> {
                    String fechaHora = etFechaHora.getText().toString().trim();

                    if (fechaHora.isEmpty()) {
                        ToastPersonalizado.mostrarError(this, getString(R.string.empty_fields));
                        return;
                    }

                    Equipo equipoLocal     = equiposList.get(spinnerLocal.getSelectedItemPosition());
                    Equipo equipoVisitante = equiposList.get(spinnerVisitante.getSelectedItemPosition());
                    Jornada jornada        = listaJornadas.get(spinnerJornada.getSelectedItemPosition());
                    String estado          = estados[spinnerEstado.getSelectedItemPosition()];

                    crearPartido(equipoLocal.getId(), equipoVisitante.getId(),
                            jornada.getId(), fechaHora, estado);
                })
                .setNegativeButton(getString(R.string.dialogo_no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void crearPartido(long equipoLocalId, long equipoVisitanteId,
                              long jornadaId, String fechaHora, String estado) {
        String token = SessionManager.getToken(this);

        try {
            JSONObject body = new JSONObject();
            body.put("equipoLocalId", equipoLocalId);
            body.put("equipoVisitanteId", equipoVisitanteId);
            body.put("jornadaId", jornadaId);
            body.put("fechaHora", fechaHora);
            body.put("estado", estado);
            body.put("golesLocal", 0);
            body.put("golesVisitante", 0);

            API.postPartido(body, token, new UtilREST.OnResponseListener() {
                @Override
                public void onSuccess(UtilREST.Response r) {
                    ToastPersonalizado.mostrarCorto(MainActivity.this,
                            getString(R.string.partido_creado));
                    cargarEquiposYPartidos();
                }

                @Override
                public void onError(UtilREST.Response r) {
                    ToastPersonalizado.mostrarErrorApi(MainActivity.this, r);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filtrarPartidos(String estado) {
        filtroActual = estado;

        List<Partido> partidosFiltrados = new ArrayList<>();
        for (Partido partido : todosPartidos) {
            if (!partido.getEstado().equals(estado)) continue;
            if (!partido.getJornadaId().equals(jornadaFiltro)) continue;
            partidosFiltrados.add(partido);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_partido, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_aniadir_partido) {
            mostrarDialogoCrearPartido();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}