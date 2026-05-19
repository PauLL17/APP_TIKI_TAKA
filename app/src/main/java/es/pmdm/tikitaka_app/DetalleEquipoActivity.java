package es.pmdm.tikitaka_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.pmdm.tikitaka_app.adapters.JugadoresAdapter;
import es.pmdm.tikitaka_app.api.API;
import es.pmdm.tikitaka_app.api.UtilJSONParser;
import es.pmdm.tikitaka_app.api.UtilREST;
import es.pmdm.tikitaka_app.modelos.Equipo;
import es.pmdm.tikitaka_app.modelos.Jugador;

public class DetalleEquipoActivity extends BaseActivity {

    private TextView tvNombre, tvCiudad, tvEstadio, tvEntrenador, tvAnio;
    private Spinner spinnerPosicion;
    private ListView lvJugadores;
    private ImageView ivEscudo;

    private long equipoId;
    private Equipo equipo;
    private List<Jugador> listaJugadores = new ArrayList<>();
    private JugadoresAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_equipo);

        equipoId = getIntent().getLongExtra("equipo_id", -1);

        initViews();
        setupToolbar();
        setupSpinner();
        cargarEquipo();
    }

    private void initViews() {
        tvNombre      = findViewById(R.id.tvNombreEquipo);
        tvCiudad      = findViewById(R.id.tvCiudadEquipo);
        tvEstadio     = findViewById(R.id.tvEstadioEquipo);
        tvEntrenador  = findViewById(R.id.tvEntrenadorEquipo);
        tvAnio        = findViewById(R.id.tvAnioFundacion);
        spinnerPosicion = findViewById(R.id.spinnerPosicion);
        lvJugadores   = findViewById(R.id.lvJugadores);
        ivEscudo = findViewById(R.id.ivEscudoEquipo);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.detalle_equipo);
        }
    }

    private void setupSpinner() {
        String[] posiciones = {
                getString(R.string.todas_posiciones),
                "PORTERO", "DEFENSA", "CENTROCAMPISTA", "DELANTERO"
        };

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                posiciones
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPosicion.setAdapter(adapterSpinner);

        spinnerPosicion.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    cargarJugadores();
                } else {
                    cargarJugadoresPorPosicion(posiciones[position]);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void cargarEquipo() {
        String token = SessionManager.getToken(this);

        API.getEquipo(equipoId, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                equipo = UtilJSONParser.parseEquipo(r.content);
                mostrarInfoEquipo();
                cargarJugadores();
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarErrorApi(DetalleEquipoActivity.this, r);
            }
        });
    }

    private void mostrarInfoEquipo() {
        tvNombre.setText(equipo.getNombre());
        tvCiudad.setText(getString(R.string.ciudad_label, equipo.getCiudad()));
        tvEstadio.setText(getString(R.string.estadio_label, equipo.getEstadio()));
        tvEntrenador.setText(getString(R.string.entrenador_label, equipo.getEntrenador()));
        tvAnio.setText(getString(R.string.anio_fundacion, equipo.getAnioFundacion()));

        Glide.with(this)
                .load(equipo.getEscudoUrl())
                .into(ivEscudo);
    }

    private void cargarJugadores() {
        String token = SessionManager.getToken(this);

        API.getJugadoresByEquipoJooq(equipoId, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                listaJugadores = UtilJSONParser.parseArrayJugadores(r.content);
                mostrarJugadores();
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarErrorApi(DetalleEquipoActivity.this, r);
            }
        });
    }

    private void cargarJugadoresPorPosicion(String posicion) {
        String token = SessionManager.getToken(this);

        API.getJugadoresByEquipoYPosicion(equipoId, posicion, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                listaJugadores = UtilJSONParser.parseArrayJugadores(r.content);
                mostrarJugadores();
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarErrorApi(DetalleEquipoActivity.this, r);
            }
        });
    }

    private void mostrarJugadores() {
        adapter = new JugadoresAdapter(this, listaJugadores);
        lvJugadores.setAdapter(adapter);

        lvJugadores.setOnItemClickListener((parent, view, position, id) -> {
            Jugador jugador = listaJugadores.get(position);
            Intent intent = new Intent(this, DetalleJugadorActivity.class);
            intent.putExtra("jugador_id", jugador.getId());
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle_equipo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_editar_equipo) {
            mostrarDialogoEditarEquipo();
            return true;
        }
        if (item.getItemId() == R.id.action_aniadir_jugador) {
            mostrarDialogoCrearJugador();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void mostrarDialogoEditarEquipo() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialogo_equipo, null);

        EditText etNombre     = dialogView.findViewById(R.id.etNombreEquipo);
        EditText etCiudad     = dialogView.findViewById(R.id.etCiudadEquipo);
        EditText etEstadio    = dialogView.findViewById(R.id.etEstadioEquipo);
        EditText etEntrenador = dialogView.findViewById(R.id.etEntrenadorEquipo);
        EditText etEscudoUrl  = dialogView.findViewById(R.id.etEscudoUrl);

        etNombre.setText(equipo.getNombre());
        etCiudad.setText(equipo.getCiudad());
        etEstadio.setText(equipo.getEstadio());
        etEntrenador.setText(equipo.getEntrenador());
        etEscudoUrl.setText(equipo.getEscudoUrl());

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.editar_equipo))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.dialogo_si), (dialog, which) -> {
                    String nombre     = etNombre.getText().toString().trim();
                    String ciudad     = etCiudad.getText().toString().trim();
                    String estadio    = etEstadio.getText().toString().trim();
                    String entrenador = etEntrenador.getText().toString().trim();
                    String escudoUrl  = etEscudoUrl.getText().toString().trim();

                    if (nombre.isEmpty() || ciudad.isEmpty()) {
                        ToastPersonalizado.mostrarError(this, getString(R.string.empty_fields));
                        return;
                    }

                    editarEquipo(nombre, ciudad, estadio, entrenador, escudoUrl);
                })
                .setNegativeButton(getString(R.string.dialogo_no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void editarEquipo(String nombre, String ciudad, String estadio, String entrenador, String escudoUrl) {
        String token = SessionManager.getToken(this);

        try {
            JSONObject body = new JSONObject();
            body.put("id", equipo.getId());
            body.put("nombre", nombre);
            body.put("ciudad", ciudad);
            body.put("estadio", estadio);
            body.put("entrenador", entrenador);
            body.put("escudoUrl", escudoUrl);
            body.put("anioFundacion", equipo.getAnioFundacion());

            API.putEquipo(equipo.getId(), body, token, new UtilREST.OnResponseListener() {
                @Override
                public void onSuccess(UtilREST.Response r) {
                    ToastPersonalizado.mostrarCorto(DetalleEquipoActivity.this,
                            getString(R.string.equipo_actualizado));
                    cargarEquipo();
                }

                @Override
                public void onError(UtilREST.Response r) {
                    ToastPersonalizado.mostrarError(DetalleEquipoActivity.this,
                            getString(R.string.error_cargar_datos));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarDialogoCrearJugador() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialogo_jugador, null);

        EditText etNombre      = dialogView.findViewById(R.id.etNombreJugador);
        EditText etApellidos   = dialogView.findViewById(R.id.etApellidosJugador);
        EditText etDorsal      = dialogView.findViewById(R.id.etDorsalJugador);
        EditText etNacionalidad = dialogView.findViewById(R.id.etNacionalidadJugador);
        Spinner  spPosicion    = dialogView.findViewById(R.id.spinnerPosicionJugador);

        String[] posiciones = {"PORTERO", "DEFENSA", "CENTROCAMPISTA", "DELANTERO"};
        ArrayAdapter<String> adapterPos = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, posiciones);
        adapterPos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPosicion.setAdapter(adapterPos);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.aniadir_jugador))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.dialogo_si), (dialog, which) -> {
                    String nombre      = etNombre.getText().toString().trim();
                    String apellidos   = etApellidos.getText().toString().trim();
                    String dorsalStr   = etDorsal.getText().toString().trim();
                    String nacionalidad = etNacionalidad.getText().toString().trim();
                    String posicion    = posiciones[spPosicion.getSelectedItemPosition()];

                    if (nombre.isEmpty() || apellidos.isEmpty() || dorsalStr.isEmpty()) {
                        ToastPersonalizado.mostrarError(this, getString(R.string.empty_fields));
                        return;
                    }

                    crearJugador(nombre, apellidos, Integer.parseInt(dorsalStr),
                            nacionalidad, posicion);
                })
                .setNegativeButton(getString(R.string.dialogo_no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void crearJugador(String nombre, String apellidos, int dorsal,
                              String nacionalidad, String posicion) {
        String token = SessionManager.getToken(this);

        try {
            JSONObject body = new JSONObject();
            body.put("nombre", nombre);
            body.put("apellidos", apellidos);
            body.put("dorsal", dorsal);
            body.put("nacionalidad", nacionalidad);
            body.put("posicion", posicion);
            body.put("equipoId", equipoId);

            API.postJugador(body, token, new UtilREST.OnResponseListener() {
                @Override
                public void onSuccess(UtilREST.Response r) {
                    new NotificationHelper(DetalleEquipoActivity.this).mostrarNotificacion(
                            getString(R.string.jugador_creado),
                            nombre + " " + apellidos,
                            NotificationHelper.NOTIF_CREAR);
                    cargarJugadores();
                }

                @Override
                public void onError(UtilREST.Response r) {
                    ToastPersonalizado.mostrarError(DetalleEquipoActivity.this,
                            getString(R.string.error_cargar_datos));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarJugadores();
    }
}