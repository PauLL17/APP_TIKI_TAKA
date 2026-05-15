package es.pmdm.tikitaka_app;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class DetallePartidoActivity extends BaseActivity{

    private TextView tvCompeticion, tvEquipoLocal, tvEquipoVisitante;
    private TextView tvGolesLocal, tvGolesVisitante, tvMinuto;
    private TextView tvNombreEquipoLocal, tvNombreEquipoVisitante;
    private View rowPosesion, rowTiros, rowTirosPuerta, rowCorners, rowFaltas;
    private ListView lvEventos, lvTitularesLocal, lvTitularesVisitante;
    private ProgressBar progressBar;

    private long partidoId;
    private Partido partido;
    private Map<Long, Jugador> jugadoresCache = new HashMap<>();

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
        tvCompeticion        = findViewById(R.id.tvCompeticion);
        tvEquipoLocal        = findViewById(R.id.tvEquipoLocal);
        tvEquipoVisitante    = findViewById(R.id.tvEquipoVisitante);
        tvGolesLocal         = findViewById(R.id.tvGolesLocal);
        tvGolesVisitante     = findViewById(R.id.tvGolesVisitante);
        tvMinuto             = findViewById(R.id.tvMinuto);
        tvNombreEquipoLocal  = findViewById(R.id.tvNombreEquipoLocal);
        tvNombreEquipoVisitante = findViewById(R.id.tvNombreEquipoVisitante);
        rowPosesion          = findViewById(R.id.rowPosesion);
        rowTiros             = findViewById(R.id.rowTiros);
        rowTirosPuerta       = findViewById(R.id.rowTirosPuerta);
        rowCorners           = findViewById(R.id.rowCorners);
        rowFaltas            = findViewById(R.id.rowFaltas);
        lvEventos            = findViewById(R.id.lvEventos);
        lvTitularesLocal     = findViewById(R.id.lvTitularesLocal);
        lvTitularesVisitante = findViewById(R.id.lvTitularesVisitante);
    }

    private void cargarPartido() {
        String token = SessionManager.getToken(this);

        API.getPartido(partidoId, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                partido = UtilJSONParser.parsePartido(r.content);
                mostrarCabecera();
                cargarJugadoresYDatos();
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarError(DetallePartidoActivity.this,
                        getString(R.string.error_cargar_datos));
            }
        });
    }

    // Cargamos primero los jugadores para poder resolver nombres en eventos y alineaciones
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
                    }

                    @Override
                    public void onError(UtilREST.Response r2) {
                        ToastPersonalizado.mostrarError(DetallePartidoActivity.this,
                                getString(R.string.error_cargar_datos));
                    }
                });
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarError(DetallePartidoActivity.this,
                        getString(R.string.error_cargar_datos));
            }
        });
    }

    private void cargarEstadisticas() {
        String token = SessionManager.getToken(this);

        API.getEstadisticasByPartido(partidoId, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                Estadistica est = UtilJSONParser.parseEstadistica(r.content);
                mostrarEstadisticas(est);
            }

            @Override
            public void onError(UtilREST.Response r) {
                // No mostramos error — puede que no haya estadísticas aún
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
            public void onError(UtilREST.Response r) {
                // No mostramos error — puede que no haya alineación aún
            }
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
            tvMinuto.setText(partido.getFechaHora());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}