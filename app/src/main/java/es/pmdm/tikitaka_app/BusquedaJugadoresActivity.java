package es.pmdm.tikitaka_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import es.pmdm.tikitaka_app.adapters.JugadoresAdapter;
import es.pmdm.tikitaka_app.api.API;
import es.pmdm.tikitaka_app.api.UtilJSONParser;
import es.pmdm.tikitaka_app.api.UtilREST;
import es.pmdm.tikitaka_app.modelos.Jugador;

public class BusquedaJugadoresActivity extends BaseActivity
{
    private EditText etBusqueda;
    private Spinner spinnerPosicion;
    private Button btnBuscar;
    private ListView lvResultados;
    private TextView tvNoResultados;

    private List<Jugador> listaJugadores = new ArrayList<>();
    private JugadoresAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_jugadores);

        initViews();
        setupToolbar();
        setupSpinner();
        setupBotonBuscar();
    }

    private void initViews() {
        etBusqueda      = findViewById(R.id.etBusqueda);
        spinnerPosicion = findViewById(R.id.spinnerPosicionBusqueda);
        btnBuscar       = findViewById(R.id.btnBuscar);
        lvResultados    = findViewById(R.id.lvResultados);
        tvNoResultados  = findViewById(R.id.tvNoResultados);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.busqueda_jugadores);
        }
    }

    private void setupSpinner() {
        String[] posiciones = {
                getString(R.string.todas_posiciones_busqueda),
                "PORTERO", "DEFENSA", "CENTROCAMPISTA", "DELANTERO"
        };

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                posiciones
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPosicion.setAdapter(adapterSpinner);
    }

    private void setupBotonBuscar() {
        btnBuscar.setOnClickListener(v -> {
            String nombre = etBusqueda.getText().toString().trim();
            int posicionIndex = spinnerPosicion.getSelectedItemPosition();
            String posicion = posicionIndex == 0 ? "" : spinnerPosicion.getSelectedItem().toString();
            buscarJugadores(nombre, posicion);
        });
    }

    private void buscarJugadores(String nombre, String posicion) {
        String token = SessionManager.getToken(this);

        API.buscarJugadoresJooq(nombre, posicion, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                listaJugadores = UtilJSONParser.parseArrayJugadores(r.content);
                mostrarResultados();
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarErrorApi(BusquedaJugadoresActivity.this, r);
            }
        });
    }

    private void mostrarResultados() {
        if (listaJugadores.isEmpty()) {
            tvNoResultados.setVisibility(View.VISIBLE);
            lvResultados.setVisibility(View.GONE);
            return;
        }

        tvNoResultados.setVisibility(View.GONE);
        lvResultados.setVisibility(View.VISIBLE);

        adapter = new JugadoresAdapter(this, listaJugadores);
        lvResultados.setAdapter(adapter);

        lvResultados.setOnItemClickListener((parent, view, position, id) -> {
            Jugador jugador = listaJugadores.get(position);
            Intent intent = new Intent(this, DetalleJugadorActivity.class);
            intent.putExtra("jugador_id", jugador.getId());
            startActivity(intent);
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