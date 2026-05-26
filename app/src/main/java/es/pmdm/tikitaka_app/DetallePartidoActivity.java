package es.pmdm.tikitaka_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.pmdm.tikitaka_app.adapters.EventoAdapter;
import es.pmdm.tikitaka_app.adapters.TitularAdapter;
import es.pmdm.tikitaka_app.api.API;
import es.pmdm.tikitaka_app.api.UtilJSONParser;
import es.pmdm.tikitaka_app.api.UtilREST;
import es.pmdm.tikitaka_app.modelos.Alineacion;
import es.pmdm.tikitaka_app.modelos.Estadistica;
import es.pmdm.tikitaka_app.modelos.Gol;
import es.pmdm.tikitaka_app.modelos.Jugador;
import es.pmdm.tikitaka_app.modelos.Partido;
import es.pmdm.tikitaka_app.modelos.Tarjeta;

public class DetallePartidoActivity extends BaseActivity {

    private TextView tvCompeticion, tvEquipoLocal, tvEquipoVisitante;
    private TextView tvGolesLocal, tvGolesVisitante, tvMinuto;
    private TextView tvNombreEquipoLocal, tvNombreEquipoVisitante;
    private View rowPosesion, rowTiros, rowTirosPuerta, rowCorners, rowFaltas;
    private ListView lvEventos, lvTitularesLocal, lvTitularesVisitante;

    private long partidoId;
    private Partido partido;
    private Map<Long, Jugador> jugadoresCache = new HashMap<>();
    private Estadistica estadisticaActual = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_partido);

        setupToolbar();
        initViews();
        partidoId = getIntent().getLongExtra("partido_id", -1);
        cargarPartido();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.detalle_partido);
        }
    }

    private void initViews() {
        tvCompeticion           = findViewById(R.id.tvCompeticion);
        tvEquipoLocal           = findViewById(R.id.tvEquipoLocal);
        tvEquipoVisitante       = findViewById(R.id.tvEquipoVisitante);
        tvGolesLocal            = findViewById(R.id.tvGolesLocal);
        tvGolesVisitante        = findViewById(R.id.tvGolesVisitante);
        tvMinuto                = findViewById(R.id.tvMinuto);
        tvNombreEquipoLocal     = findViewById(R.id.tvNombreEquipoLocal);
        tvNombreEquipoVisitante = findViewById(R.id.tvNombreEquipoVisitante);
        rowPosesion             = findViewById(R.id.rowPosesion);
        rowTiros                = findViewById(R.id.rowTiros);
        rowTirosPuerta          = findViewById(R.id.rowTirosPuerta);
        rowCorners              = findViewById(R.id.rowCorners);
        rowFaltas               = findViewById(R.id.rowFaltas);
        lvEventos               = findViewById(R.id.lvEventos);
        lvTitularesLocal        = findViewById(R.id.lvTitularesLocal);
        lvTitularesVisitante    = findViewById(R.id.lvTitularesVisitante);
    }

    private void cargarPartido() {
        String token = SessionManager.getToken(this);

        API.getPartido(partidoId, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                partido = UtilJSONParser.parsePartido(r.content);

                API.getEquipo(partido.getEquipoLocalId(), token, new UtilREST.OnResponseListener() {
                    @Override
                    public void onSuccess(UtilREST.Response r2) {
                        partido.setEquipoLocal(UtilJSONParser.parseEquipo(r2.content));

                        API.getEquipo(partido.getEquipoVisitanteId(), token, new UtilREST.OnResponseListener() {
                            @Override
                            public void onSuccess(UtilREST.Response r3) {
                                partido.setEquipoVisitante(UtilJSONParser.parseEquipo(r3.content));
                                mostrarCabecera();
                                cargarJugadoresYDatos();
                            }

                            @Override
                            public void onError(UtilREST.Response r3) {
                                mostrarCabecera();
                                cargarJugadoresYDatos();
                            }
                        });
                    }

                    @Override
                    public void onError(UtilREST.Response r2) {
                        mostrarCabecera();
                        cargarJugadoresYDatos();
                    }
                });
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarErrorApi(DetallePartidoActivity.this, r);
            }
        });
    }

    private void cargarJugadoresYDatos() {
        String token = SessionManager.getToken(this);

        API.getJugadoresByEquipo(partido.getEquipoLocalId(), token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                List<Jugador> jugadores = UtilJSONParser.parseArrayJugadores(r.content);
                for (Jugador j : jugadores) {
                    jugadoresCache.put(j.getId(), j);
                }

                API.getJugadoresByEquipo(partido.getEquipoVisitanteId(), token, new UtilREST.OnResponseListener() {
                    @Override
                    public void onSuccess(UtilREST.Response r2) {
                        List<Jugador> jugadores2 = UtilJSONParser.parseArrayJugadores(r2.content);
                        for (Jugador j : jugadores2) {
                            jugadoresCache.put(j.getId(), j);
                        }
                        cargarEstadisticas();
                        cargarEventos();
                        cargarAlineaciones();
                        conectarWebSocketMarcador();
                    }

                    @Override
                    public void onError(UtilREST.Response r2) {
                        ToastPersonalizado.mostrarErrorApi(DetallePartidoActivity.this, r);
                    }
                });
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarErrorApi(DetallePartidoActivity.this, r);
            }
        });
    }

    private void conectarWebSocketMarcador() {
        WebSocketManager.getInstance().conectarMarcador(partidoId, (golesLocal, golesVisitante, estado) -> {
            tvGolesLocal.setText(String.valueOf(golesLocal));
            tvGolesVisitante.setText(String.valueOf(golesVisitante));

            if (estado.equals(Partido.ESTADO_EN_VIVO)) {
                tvMinuto.setText(getString(R.string.live_matches));
            } else if (estado.equals(Partido.ESTADO_FINALIZADO)) {
                tvMinuto.setText("FT");
            } else {
                tvMinuto.setText(formatearFecha(partido.getFechaHora()));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebSocketManager.getInstance().desconectarMarcador();
    }

    private void cargarEstadisticas() {
        String token = SessionManager.getToken(this);

        API.getEstadisticasByPartido(partidoId, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                estadisticaActual = UtilJSONParser.parseEstadistica(r.content);
                mostrarEstadisticas(estadisticaActual);
            }

            @Override
            public void onError(UtilREST.Response r) {
                estadisticaActual = null;
            }
        });
    }

    private void cargarEventos() {
        String token = SessionManager.getToken(this);

        API.getGolesByPartido(partidoId, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                List<Gol> goles = UtilJSONParser.parseArrayGoles(r.content);

                API.getTarjetasByPartido(partidoId, token, new UtilREST.OnResponseListener() {
                    @Override
                    public void onSuccess(UtilREST.Response r2) {
                        List<Tarjeta> tarjetas = UtilJSONParser.parseArrayTarjetas(r2.content);
                        mostrarEventos(goles, tarjetas);
                    }

                    @Override
                    public void onError(UtilREST.Response r2) {
                        mostrarEventos(goles, new ArrayList<>());
                    }
                });
            }

            @Override
            public void onError(UtilREST.Response r) {
                mostrarEventos(new ArrayList<>(), new ArrayList<>());
            }
        });
    }

    private void cargarAlineaciones() {
        String token = SessionManager.getToken(this);

        API.getTitularesByPartido(partidoId, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                List<Alineacion> alineaciones = UtilJSONParser.parseArrayAlineaciones(r.content);
                mostrarAlineaciones(alineaciones);
            }

            @Override
            public void onError(UtilREST.Response r) {}
        });
    }

    private void mostrarCabecera() {
        if (partido.getEquipoLocal() != null) {
            tvEquipoLocal.setText(partido.getEquipoLocal().getNombre());
            tvNombreEquipoLocal.setText(partido.getEquipoLocal().getNombre());
        } else {
            tvEquipoLocal.setText("Local");
            tvNombreEquipoLocal.setText("Local");
        }

        if (partido.getEquipoVisitante() != null) {
            tvEquipoVisitante.setText(partido.getEquipoVisitante().getNombre());
            tvNombreEquipoVisitante.setText(partido.getEquipoVisitante().getNombre());
        } else {
            tvEquipoVisitante.setText("Visitante");
            tvNombreEquipoVisitante.setText("Visitante");
        }

        tvGolesLocal.setText(String.valueOf(partido.getGolesLocal()));
        tvGolesVisitante.setText(String.valueOf(partido.getGolesVisitante()));

        if (partido.isEnVivo()) {
            tvMinuto.setText(partido.getMinutoActual() + "'");
        } else if (partido.isFinalizado()) {
            tvMinuto.setText("FT");
        } else {
            tvMinuto.setText(formatearFecha(partido.getFechaHora()));
        }
    }

    private String formatearFecha(String fechaHora) {
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date fecha = formatoEntrada.parse(fechaHora);
            return formatoSalida.format(fecha);
        } catch (Exception e) {
            return fechaHora;
        }
    }

    private void mostrarEstadisticas(Estadistica est) {
        setFilaEstadistica(rowPosesion,
                est.getPosesionLocal() + "%",
                getString(R.string.posesion),
                est.getPosesionVisitante() + "%");

        setFilaEstadistica(rowTiros,
                String.valueOf(est.getTirosLocal()),
                getString(R.string.tiros),
                String.valueOf(est.getTirosVisitante()));

        setFilaEstadistica(rowTirosPuerta,
                String.valueOf(est.getTirosAPuertaLocal()),
                getString(R.string.tiros_puerta),
                String.valueOf(est.getTirosAPuertaVisitante()));

        setFilaEstadistica(rowCorners,
                String.valueOf(est.getCornersLocal()),
                getString(R.string.corners),
                String.valueOf(est.getCornersVisitante()));

        setFilaEstadistica(rowFaltas,
                String.valueOf(est.getFaltasLocal()),
                getString(R.string.faltas),
                String.valueOf(est.getFaltasVisitante()));
    }

    private void setFilaEstadistica(View row, String valorLocal,
                                    String etiqueta, String valorVisitante) {
        ((TextView) row.findViewById(R.id.tvValorLocal)).setText(valorLocal);
        ((TextView) row.findViewById(R.id.tvEtiqueta)).setText(etiqueta);
        ((TextView) row.findViewById(R.id.tvValorVisitante)).setText(valorVisitante);
    }

    private void mostrarEventos(List<Gol> goles, List<Tarjeta> tarjetas) {
        List<String> eventos = new ArrayList<>();

        for (Gol gol : goles) {
            Jugador jugador = jugadoresCache.get(gol.getJugadorId());
            String nombre;
            if (jugador != null) {
                nombre = jugador.getNombreCompleto();
            } else {
                nombre = "Desconocido";
            }
            String icono;
            if (Gol.TIPO_EN_PROPIA.equals(gol.getTipo())) {
                icono = "⚽(pp)";
            } else {
                icono = "⚽";
            }
            eventos.add(gol.getMinuto() + "' " + icono + " " + nombre);
        }

        for (Tarjeta tarjeta : tarjetas) {
            Jugador jugador = jugadoresCache.get(tarjeta.getJugadorId());
            String nombre;
            if (jugador != null) {
                nombre = jugador.getNombreCompleto();
            } else {
                nombre = "Desconocido";
            }
            String icono;
            if (Tarjeta.TIPO_ROJA.equals(tarjeta.getTipo())) {
                icono = "🔴";
            } else {
                icono = "🟡";
            }
            eventos.add(tarjeta.getMinuto() + "' " + icono + " " + nombre);
        }

        EventoAdapter adapter = new EventoAdapter(this, eventos);
        lvEventos.setAdapter(adapter);
    }

    private void mostrarAlineaciones(List<Alineacion> alineaciones) {
        List<Jugador> titularesLocal     = new ArrayList<>();
        List<Jugador> titularesVisitante = new ArrayList<>();

        for (Alineacion alineacion : alineaciones) {
            Jugador jugador = jugadoresCache.get(alineacion.getJugadorId());
            if (jugador == null) continue;

            if (jugador.getEquipoId().equals(partido.getEquipoLocalId())) {
                titularesLocal.add(jugador);
            } else {
                titularesVisitante.add(jugador);
            }
        }

        lvTitularesLocal.setAdapter(new TitularAdapter(this, titularesLocal));
        lvTitularesVisitante.setAdapter(new TitularAdapter(this, titularesVisitante));
    }

    private void mostrarDialogoAnadirGol() {
        if (jugadoresCache.isEmpty()) {
            ToastPersonalizado.mostrarError(this, getString(R.string.error_cargar_datos));
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialogo_gol, null);

        Spinner spinnerJugador  = dialogView.findViewById(R.id.spinnerJugador);
        EditText etMinuto       = dialogView.findViewById(R.id.etMinuto);
        Spinner spinnerTipoGol  = dialogView.findViewById(R.id.spinnerTipoGol);

        List<String> nombresJugadores = new ArrayList<>();
        List<Jugador> jugadoresList   = new ArrayList<>(jugadoresCache.values());
        for (Jugador j : jugadoresList) {
            nombresJugadores.add(j.getNombreCompleto());
        }

        ArrayAdapter<String> adapterJugadores = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombresJugadores);
        adapterJugadores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJugador.setAdapter(adapterJugadores);

        String[] tiposGol = {"NORMAL", "PENALTI", "EN_PROPIA"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, tiposGol);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoGol.setAdapter(adapterTipo);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.aniadir_gol))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.dialogo_si), (dialog, which) -> {
                    String minutoStr = etMinuto.getText().toString().trim();
                    if (minutoStr.isEmpty()) {
                        ToastPersonalizado.mostrarError(this, getString(R.string.empty_fields));
                        return;
                    }
                    Jugador jugadorSeleccionado = jugadoresList.get(spinnerJugador.getSelectedItemPosition());
                    String tipoGol = tiposGol[spinnerTipoGol.getSelectedItemPosition()];
                    int minuto = Integer.parseInt(minutoStr);
                    anadirGol(jugadorSeleccionado.getId(), minuto, tipoGol);
                })
                .setNegativeButton(getString(R.string.dialogo_no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void anadirGol(long jugadorId, int minuto, String tipo) {
        String token = SessionManager.getToken(this);

        try {
            JSONObject body = new JSONObject();
            body.put("partidoId", partidoId);
            body.put("jugadorId", jugadorId);
            body.put("minuto", minuto);
            body.put("tipo", tipo);

            API.postGol(body, token, new UtilREST.OnResponseListener() {
                @Override
                public void onSuccess(UtilREST.Response r) {
                    ToastPersonalizado.mostrarCorto(DetallePartidoActivity.this,
                            getString(R.string.gol_creado));
                    cargarEventos();
                    cargarPartido();
                }

                @Override
                public void onError(UtilREST.Response r) {
                    ToastPersonalizado.mostrarErrorApi(DetallePartidoActivity.this, r);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarDialogoEditarPartido() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialogo_partido, null);

        Spinner spinnerEstado   = dialogView.findViewById(R.id.spinnerEstado);
        EditText etMinutoActual = dialogView.findViewById(R.id.etMinutoActual);
        EditText etFechaHora = dialogView.findViewById(R.id.etFechaHora);

        String[] estados = {"PROGRAMADO", "EN_VIVO", "FINALIZADO"};
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, estados);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);

        for (int i = 0; i < estados.length; i++) {
            if (estados[i].equals(partido.getEstado())) {
                spinnerEstado.setSelection(i);
                break;
            }
        }

        etMinutoActual.setText(String.valueOf(partido.getMinutoActual()));
        etFechaHora.setText(partido.getFechaHora());

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.editar_partido))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.dialogo_si), (dialog, which) -> {
                    String estado    = estados[spinnerEstado.getSelectedItemPosition()];
                    String minutoStr = etMinutoActual.getText().toString().trim();
                    String fechaHora = etFechaHora.getText().toString().trim().replace(" ", "T");
                    int minutoActual = minutoStr.isEmpty() ? 0 : Integer.parseInt(minutoStr);
                    editarPartido(estado, minutoActual, fechaHora);
                })
                .setNegativeButton(getString(R.string.dialogo_no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void editarPartido(String estado, int minutoActual, String fechaHora) {
        String token = SessionManager.getToken(this);

        try {
            JSONObject body = new JSONObject();
            body.put("id", partidoId);
            body.put("equipoLocalId", partido.getEquipoLocalId());
            body.put("equipoVisitanteId", partido.getEquipoVisitanteId());
            body.put("jornadaId", partido.getJornadaId());
            body.put("fechaHora", fechaHora.isEmpty() ? partido.getFechaHora() : fechaHora);
            body.put("estado", estado);
            body.put("golesLocal", partido.getGolesLocal());
            body.put("golesVisitante", partido.getGolesVisitante());
            body.put("minutoActual", minutoActual);

            API.putPartido(partidoId, body, token, new UtilREST.OnResponseListener() {
                @Override
                public void onSuccess(UtilREST.Response r) {
                    ToastPersonalizado.mostrarCorto(DetallePartidoActivity.this,
                            getString(R.string.partido_actualizado));
                    cargarPartido();
                }

                @Override
                public void onError(UtilREST.Response r) {
                    ToastPersonalizado.mostrarErrorApi(DetallePartidoActivity.this, r);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void mostrarDialogoAnadirTitular() {
        if (jugadoresCache.isEmpty()) {
            ToastPersonalizado.mostrarError(this, getString(R.string.error_cargar_datos));
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialogo_gol, null);

        Spinner spinnerJugador = dialogView.findViewById(R.id.spinnerJugador);
        // Oculto el resto de campos que no necesito
        dialogView.findViewById(R.id.etMinuto).setVisibility(View.GONE);
        dialogView.findViewById(R.id.spinnerTipoGol).setVisibility(View.GONE);

        List<Jugador> jugadoresList = new ArrayList<>(jugadoresCache.values());
        List<String> nombresJugadores = new ArrayList<>();
        for (Jugador j : jugadoresList) {
            nombresJugadores.add(j.getNombreCompleto());
        }

        ArrayAdapter<String> adapterJugadores = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombresJugadores);
        adapterJugadores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJugador.setAdapter(adapterJugadores);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.aniadir_titular))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.dialogo_si), (dialog, which) -> {
                    Jugador jugadorSeleccionado = jugadoresList.get(spinnerJugador.getSelectedItemPosition());
                    anadirTitular(jugadorSeleccionado.getId());
                })
                .setNegativeButton(getString(R.string.dialogo_no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void anadirTitular(long jugadorId) {
        String token = SessionManager.getToken(this);

        try {
            JSONObject body = new JSONObject();
            body.put("partidoId", partidoId);
            body.put("jugadorId", jugadorId);
            body.put("titular", true);

            API.postAlineacion(body, token, new UtilREST.OnResponseListener() {
                @Override
                public void onSuccess(UtilREST.Response r) {
                    ToastPersonalizado.mostrarCorto(DetallePartidoActivity.this,
                            getString(R.string.titular_aniadido));
                    cargarAlineaciones();
                }

                @Override
                public void onError(UtilREST.Response r) {
                    ToastPersonalizado.mostrarErrorApi(DetallePartidoActivity.this, r);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarDialogoGestionarEstadisticas() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialogo_estadisticas, null);

        EditText etPosesionLocal       = dialogView.findViewById(R.id.etPosesionLocal);
        EditText etPosesionVisitante   = dialogView.findViewById(R.id.etPosesionVisitante);
        EditText etTirosLocal          = dialogView.findViewById(R.id.etTirosLocal);
        EditText etTirosVisitante      = dialogView.findViewById(R.id.etTirosVisitante);
        EditText etTirosPuertaLocal    = dialogView.findViewById(R.id.etTirosPuertaLocal);
        EditText etTirosPuertaVisitante = dialogView.findViewById(R.id.etTirosPuertaVisitante);
        EditText etCornersLocal        = dialogView.findViewById(R.id.etCornersLocal);
        EditText etCornersVisitante    = dialogView.findViewById(R.id.etCornersVisitante);
        EditText etFaltasLocal         = dialogView.findViewById(R.id.etFaltasLocal);
        EditText etFaltasVisitante     = dialogView.findViewById(R.id.etFaltasVisitante);

        if (estadisticaActual != null) {
            etPosesionLocal.setText(String.valueOf(estadisticaActual.getPosesionLocal()));
            etPosesionVisitante.setText(String.valueOf(estadisticaActual.getPosesionVisitante()));
            etTirosLocal.setText(String.valueOf(estadisticaActual.getTirosLocal()));
            etTirosVisitante.setText(String.valueOf(estadisticaActual.getTirosVisitante()));
            etTirosPuertaLocal.setText(String.valueOf(estadisticaActual.getTirosAPuertaLocal()));
            etTirosPuertaVisitante.setText(String.valueOf(estadisticaActual.getTirosAPuertaVisitante()));
            etCornersLocal.setText(String.valueOf(estadisticaActual.getCornersLocal()));
            etCornersVisitante.setText(String.valueOf(estadisticaActual.getCornersVisitante()));
            etFaltasLocal.setText(String.valueOf(estadisticaActual.getFaltasLocal()));
            etFaltasVisitante.setText(String.valueOf(estadisticaActual.getFaltasVisitante()));
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.gestionar_estadisticas))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.dialogo_si), (dialog, which) -> {
                    try {
                        int posesionLocal        = Integer.parseInt(etPosesionLocal.getText().toString().trim().isEmpty() ? "0" : etPosesionLocal.getText().toString().trim());
                        int posesionVisitante    = Integer.parseInt(etPosesionVisitante.getText().toString().trim().isEmpty() ? "0" : etPosesionVisitante.getText().toString().trim());
                        int tirosLocal           = Integer.parseInt(etTirosLocal.getText().toString().trim().isEmpty() ? "0" : etTirosLocal.getText().toString().trim());
                        int tirosVisitante       = Integer.parseInt(etTirosVisitante.getText().toString().trim().isEmpty() ? "0" : etTirosVisitante.getText().toString().trim());
                        int tirosPuertaLocal     = Integer.parseInt(etTirosPuertaLocal.getText().toString().trim().isEmpty() ? "0" : etTirosPuertaLocal.getText().toString().trim());
                        int tirosPuertaVisitante = Integer.parseInt(etTirosPuertaVisitante.getText().toString().trim().isEmpty() ? "0" : etTirosPuertaVisitante.getText().toString().trim());
                        int cornersLocal         = Integer.parseInt(etCornersLocal.getText().toString().trim().isEmpty() ? "0" : etCornersLocal.getText().toString().trim());
                        int cornersVisitante     = Integer.parseInt(etCornersVisitante.getText().toString().trim().isEmpty() ? "0" : etCornersVisitante.getText().toString().trim());
                        int faltasLocal          = Integer.parseInt(etFaltasLocal.getText().toString().trim().isEmpty() ? "0" : etFaltasLocal.getText().toString().trim());
                        int faltasVisitante      = Integer.parseInt(etFaltasVisitante.getText().toString().trim().isEmpty() ? "0" : etFaltasVisitante.getText().toString().trim());

                        gestionarEstadisticas(posesionLocal, posesionVisitante, tirosLocal, tirosVisitante,
                                tirosPuertaLocal, tirosPuertaVisitante, cornersLocal, cornersVisitante,
                                faltasLocal, faltasVisitante);
                    } catch (NumberFormatException e) {
                        ToastPersonalizado.mostrarError(DetallePartidoActivity.this, getString(R.string.empty_fields));
                    }
                })
                .setNegativeButton(getString(R.string.dialogo_no), (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void gestionarEstadisticas(int posesionLocal, int posesionVisitante,
                                       int tirosLocal, int tirosVisitante,
                                       int tirosPuertaLocal, int tirosPuertaVisitante,
                                       int cornersLocal, int cornersVisitante,
                                       int faltasLocal, int faltasVisitante) {
        String token = SessionManager.getToken(this);

        try {
            JSONObject body = new JSONObject();
            body.put("partidoId", partidoId);
            body.put("posesionLocal", posesionLocal);
            body.put("posesionVisitante", posesionVisitante);
            body.put("tirosLocal", tirosLocal);
            body.put("tirosVisitante", tirosVisitante);
            body.put("tirosAPuertaLocal", tirosPuertaLocal);
            body.put("tirosAPuertaVisitante", tirosPuertaVisitante);
            body.put("cornersLocal", cornersLocal);
            body.put("cornersVisitante", cornersVisitante);
            body.put("faltasLocal", faltasLocal);
            body.put("faltasVisitante", faltasVisitante);

            if (estadisticaActual == null) {
                API.postEstadistica(body, token, new UtilREST.OnResponseListener() {
                    @Override
                    public void onSuccess(UtilREST.Response r) {
                        ToastPersonalizado.mostrarCorto(DetallePartidoActivity.this,
                                getString(R.string.estadisticas_guardadas));
                        cargarEstadisticas();
                    }

                    @Override
                    public void onError(UtilREST.Response r) {
                        ToastPersonalizado.mostrarErrorApi(DetallePartidoActivity.this, r);
                    }
                });
            } else {
                body.put("id", estadisticaActual.getId());
                API.putEstadistica(body, token, new UtilREST.OnResponseListener() {
                    @Override
                    public void onSuccess(UtilREST.Response r) {
                        ToastPersonalizado.mostrarCorto(DetallePartidoActivity.this,
                                getString(R.string.estadisticas_guardadas));
                        cargarEstadisticas();
                    }

                    @Override
                    public void onError(UtilREST.Response r) {
                        ToastPersonalizado.mostrarErrorApi(DetallePartidoActivity.this, r);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarDialogoAnadirTarjeta() {
        if (jugadoresCache.isEmpty()) {
            ToastPersonalizado.mostrarError(this, getString(R.string.error_cargar_datos));
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialogo_gol, null);

        Spinner spinnerJugador = dialogView.findViewById(R.id.spinnerJugador);
        EditText etMinuto      = dialogView.findViewById(R.id.etMinuto);
        Spinner spinnerTipo    = dialogView.findViewById(R.id.spinnerTipoGol);

        List<String> nombresJugadores = new ArrayList<>();
        List<Jugador> jugadoresList   = new ArrayList<>(jugadoresCache.values());
        for (Jugador j : jugadoresList) {
            nombresJugadores.add(j.getNombreCompleto());
        }

        ArrayAdapter<String> adapterJugadores = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombresJugadores);
        adapterJugadores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJugador.setAdapter(adapterJugadores);

        String[] tiposTarjeta = {"AMARILLA", "ROJA"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, tiposTarjeta);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipo);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.aniadir_tarjeta))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.dialogo_si), (dialog, which) -> {
                    String minutoStr = etMinuto.getText().toString().trim();
                    if (minutoStr.isEmpty()) {
                        ToastPersonalizado.mostrarError(this, getString(R.string.empty_fields));
                        return;
                    }
                    Jugador jugadorSeleccionado = jugadoresList.get(spinnerJugador.getSelectedItemPosition());
                    String tipo = tiposTarjeta[spinnerTipo.getSelectedItemPosition()];
                    int minuto = Integer.parseInt(minutoStr);
                    anadirTarjeta(jugadorSeleccionado.getId(), minuto, tipo);
                })
                .setNegativeButton(getString(R.string.dialogo_no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void anadirTarjeta(long jugadorId, int minuto, String tipo) {
        String token = SessionManager.getToken(this);

        try {
            JSONObject body = new JSONObject();
            body.put("partidoId", partidoId);
            body.put("jugadorId", jugadorId);
            body.put("minuto", minuto);
            body.put("tipo", tipo);

            API.postTarjeta(body, token, new UtilREST.OnResponseListener() {
                @Override
                public void onSuccess(UtilREST.Response r) {
                    ToastPersonalizado.mostrarCorto(DetallePartidoActivity.this,
                            getString(R.string.tarjeta_anadida));
                    cargarPartido();
                }

                @Override
                public void onError(UtilREST.Response r) {
                    ToastPersonalizado.mostrarErrorApi(DetallePartidoActivity.this, r);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle_partido, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_aniadir_gol) {
            mostrarDialogoAnadirGol();
            return true;
        }
        if (item.getItemId() == R.id.action_editar_partido) {
            mostrarDialogoEditarPartido();
            return true;
        }

        if (item.getItemId() == R.id.action_aniadir_titular) {
            mostrarDialogoAnadirTitular();
            return true;
        }

        if (item.getItemId() == R.id.action_gestionar_estadisticas) {
            mostrarDialogoGestionarEstadisticas();
            return true;
        }

        if (item.getItemId() == R.id.action_aniadir_tarjeta) {
            mostrarDialogoAnadirTarjeta();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}