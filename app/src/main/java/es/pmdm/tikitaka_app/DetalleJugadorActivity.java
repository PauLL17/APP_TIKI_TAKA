package es.pmdm.tikitaka_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONObject;

import java.util.List;

import es.pmdm.tikitaka_app.api.API;
import es.pmdm.tikitaka_app.api.UtilJSONParser;
import es.pmdm.tikitaka_app.api.UtilREST;
import es.pmdm.tikitaka_app.modelos.Gol;
import es.pmdm.tikitaka_app.modelos.Jugador;
import es.pmdm.tikitaka_app.modelos.Tarjeta;

public class DetalleJugadorActivity extends AppCompatActivity {

    private TextView tvNombre, tvDorsal, tvPosicion, tvNacionalidad;
    private TextView tvTotalGoles, tvTotalAmarillos, tvTotalRojos;
    private Button btnEditar, btnEliminar;

    private long jugadorId;
    private Jugador jugador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_jugador);

        jugadorId = getIntent().getLongExtra("jugador_id", -1);

        initViews();
        setupToolbar();
        setupListeners();
        cargarJugador();
    }

    private void initViews() {
        tvNombre        = findViewById(R.id.tvNombreJugador);
        tvDorsal        = findViewById(R.id.tvDorsalJugador);
        tvPosicion      = findViewById(R.id.tvPosicionJugador);
        tvNacionalidad  = findViewById(R.id.tvNacionalidadJugador);
        tvTotalGoles    = findViewById(R.id.tvTotalGoles);
        tvTotalAmarillos = findViewById(R.id.tvTotalAmarillos);
        tvTotalRojos    = findViewById(R.id.tvTotalRojos);
        btnEditar       = findViewById(R.id.btnEditarJugador);
        btnEliminar     = findViewById(R.id.btnEliminarJugador);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.detalle_jugador);
        }
    }

    private void setupListeners() {
        btnEditar.setOnClickListener(v -> mostrarDialogoEditarJugador());
        btnEliminar.setOnClickListener(v -> mostrarDialogoEliminar());
    }

    private void cargarJugador() {
        String token = SessionManager.getToken(this);

        API.getJugador(jugadorId, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                jugador = UtilJSONParser.parseJugador(r.content);
                mostrarInfoJugador();
                cargarEstadisticas();
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarError(DetalleJugadorActivity.this,
                        getString(R.string.error_cargar_datos));
            }
        });
    }

    private void mostrarInfoJugador() {
        tvNombre.setText(jugador.getNombreCompleto());
        tvDorsal.setText(getString(R.string.dorsal_label, jugador.getDorsal()));
        tvPosicion.setText(getString(R.string.posicion_label, jugador.getPosicion()));
        tvNacionalidad.setText(getString(R.string.nacionalidad_label, jugador.getNacionalidad()));
    }

    private void cargarEstadisticas() {
        String token = SessionManager.getToken(this);

        API.getGolesByJugador(jugadorId, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                List<Gol> goles = UtilJSONParser.parseArrayGoles(r.content);
                tvTotalGoles.setText(String.valueOf(goles.size()));
            }

            @Override
            public void onError(UtilREST.Response r) {
                tvTotalGoles.setText("0");
            }
        });

        API.getTarjetasByJugador(jugadorId, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                List<Tarjeta> tarjetas = UtilJSONParser.parseArrayTarjetas(r.content);
                int amarillas = 0;
                int rojas     = 0;

                for (Tarjeta tarjeta : tarjetas) {
                    if (Tarjeta.TIPO_AMARILLA.equals(tarjeta.getTipo())) {
                        amarillas++;
                    } else if (Tarjeta.TIPO_ROJA.equals(tarjeta.getTipo())) {
                        rojas++;
                    }
                }

                tvTotalAmarillos.setText(String.valueOf(amarillas));
                tvTotalRojos.setText(String.valueOf(rojas));
            }

            @Override
            public void onError(UtilREST.Response r) {
                tvTotalAmarillos.setText("0");
                tvTotalRojos.setText("0");
            }
        });
    }

    private void mostrarDialogoEditarJugador() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialogo_jugador, null);

        EditText etNombre       = dialogView.findViewById(R.id.etNombreJugador);
        EditText etApellidos    = dialogView.findViewById(R.id.etApellidosJugador);
        EditText etDorsal       = dialogView.findViewById(R.id.etDorsalJugador);
        EditText etNacionalidad = dialogView.findViewById(R.id.etNacionalidadJugador);
        Spinner  spPosicion     = dialogView.findViewById(R.id.spinnerPosicionJugador);

        String[] posiciones = {"PORTERO", "DEFENSA", "CENTROCAMPISTA", "DELANTERO"};
        ArrayAdapter<String> adapterPos = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, posiciones);
        adapterPos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPosicion.setAdapter(adapterPos);

        etNombre.setText(jugador.getNombre());
        etApellidos.setText(jugador.getApellidos());
        etDorsal.setText(String.valueOf(jugador.getDorsal()));
        etNacionalidad.setText(jugador.getNacionalidad());

        for (int i = 0; i < posiciones.length; i++) {
            if (posiciones[i].equals(jugador.getPosicion())) {
                spPosicion.setSelection(i);
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.editar_jugador))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.dialogo_si), (dialog, which) -> {
                    String nombre       = etNombre.getText().toString().trim();
                    String apellidos    = etApellidos.getText().toString().trim();
                    String dorsalStr    = etDorsal.getText().toString().trim();
                    String nacionalidad = etNacionalidad.getText().toString().trim();
                    String posicion     = posiciones[spPosicion.getSelectedItemPosition()];

                    if (nombre.isEmpty() || apellidos.isEmpty() || dorsalStr.isEmpty()) {
                        ToastPersonalizado.mostrarError(this, getString(R.string.empty_fields));
                        return;
                    }

                    editarJugador(nombre, apellidos, Integer.parseInt(dorsalStr),
                            nacionalidad, posicion);
                })
                .setNegativeButton(getString(R.string.dialogo_no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void editarJugador(String nombre, String apellidos, int dorsal,
                               String nacionalidad, String posicion) {
        String token = SessionManager.getToken(this);

        try {
            JSONObject body = new JSONObject();
            body.put("id", jugador.getId());
            body.put("nombre", nombre);
            body.put("apellidos", apellidos);
            body.put("dorsal", dorsal);
            body.put("nacionalidad", nacionalidad);
            body.put("posicion", posicion);
            body.put("equipoId", jugador.getEquipoId());

            API.putJugador(jugador.getId(), body, token, new UtilREST.OnResponseListener() {
                @Override
                public void onSuccess(UtilREST.Response r) {
                    ToastPersonalizado.mostrarCorto(DetalleJugadorActivity.this,
                            getString(R.string.jugador_actualizado));
                    cargarJugador();
                }

                @Override
                public void onError(UtilREST.Response r) {
                    ToastPersonalizado.mostrarError(DetalleJugadorActivity.this,
                            getString(R.string.error_cargar_datos));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarDialogoEliminar() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialogo_eliminar_jugador_titulo))
                .setMessage(getString(R.string.dialogo_eliminar_jugador_mensaje))
                .setPositiveButton(getString(R.string.dialogo_si), (dialog, which) -> {
                    eliminarJugador();
                })
                .setNegativeButton(getString(R.string.dialogo_no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void eliminarJugador() {
        String token = SessionManager.getToken(this);

        API.deleteJugador(jugadorId, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                ToastPersonalizado.mostrarCorto(DetalleJugadorActivity.this,
                        getString(R.string.jugador_eliminado));
                finish();
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarError(DetalleJugadorActivity.this,
                        getString(R.string.error_cargar_datos));
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