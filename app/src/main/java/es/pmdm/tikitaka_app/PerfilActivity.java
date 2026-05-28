package es.pmdm.tikitaka_app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.pmdm.tikitaka_app.api.API;
import es.pmdm.tikitaka_app.api.UtilJSONParser;
import es.pmdm.tikitaka_app.api.UtilREST;
import es.pmdm.tikitaka_app.modelos.Equipo;
import es.pmdm.tikitaka_app.utilidades.BaseActivity;
import es.pmdm.tikitaka_app.utilidades.SessionManager;
import es.pmdm.tikitaka_app.utilidades.ToastPersonalizado;

public class PerfilActivity extends BaseActivity {

    private TextView tvNombreUsuario, tvEmailUsuario;
    private Spinner spinnerEquipoFavorito;
    private Button btnGuardar;

    private List<Equipo> listaEquipos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        initViews();
        setupToolbar();
        setupListeners();
        mostrarDatosUsuario();
        cargarEquipos();
    }

    private void initViews() {
        tvNombreUsuario      = findViewById(R.id.tvNombreUsuario);
        tvEmailUsuario       = findViewById(R.id.tvEmailUsuario);
        spinnerEquipoFavorito = findViewById(R.id.spinnerEquipoFavorito);
        btnGuardar           = findViewById(R.id.btnGuardarPerfil);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.perfil);
        }
    }

    private void setupListeners() {
        btnGuardar.setOnClickListener(v -> guardarPerfil());
    }

    private void mostrarDatosUsuario() {
        tvNombreUsuario.setText(getString(R.string.nombre_usuario_label,
                SessionManager.getNombreUsuario(this)));
        tvEmailUsuario.setText(getString(R.string.email_label,
                SessionManager.getEmail(this)));
    }

    private void cargarEquipos() {
        String token = SessionManager.getToken(this);

        API.getEquipos(token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                listaEquipos = UtilJSONParser.parseArrayEquipos(r.content);

                List<String> nombres = new ArrayList<>();
                for (Equipo equipo : listaEquipos) {
                    nombres.add(equipo.getNombre());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        PerfilActivity.this,
                        android.R.layout.simple_spinner_item,
                        nombres
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEquipoFavorito.setAdapter(adapter);

                // Seleccionar el equipo favorito actual
                long equipoFavoritoId = SessionManager.getEquipoFavoritoId(PerfilActivity.this);
                for (int i = 0; i < listaEquipos.size(); i++) {
                    if (listaEquipos.get(i).getId().equals(equipoFavoritoId)) {
                        spinnerEquipoFavorito.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarErrorApi(PerfilActivity.this, r);
            }
        });
    }

    private void guardarPerfil() {
        if (listaEquipos.isEmpty()) {
            ToastPersonalizado.mostrarError(this, getString(R.string.error_cargar_datos));
            return;
        }

        Equipo equipoSeleccionado = listaEquipos.get(spinnerEquipoFavorito.getSelectedItemPosition());
        String token = SessionManager.getToken(this);
        long usuarioId = SessionManager.getUsuarioId(this);

        try {
            JSONObject body = new JSONObject();
            body.put("id", usuarioId);
            body.put("nombreUsuario", SessionManager.getNombreUsuario(this));
            body.put("email", SessionManager.getEmail(this));
            body.put("equipoFavoritoId", equipoSeleccionado.getId());

            API.putUsuario(usuarioId, body, token, new UtilREST.OnResponseListener() {
                @Override
                public void onSuccess(UtilREST.Response r) {
                    SessionManager.actualizarEquipoFavorito(PerfilActivity.this,
                            equipoSeleccionado.getId());
                    ToastPersonalizado.mostrarCorto(PerfilActivity.this,
                            getString(R.string.perfil_actualizado));
                }

                @Override
                public void onError(UtilREST.Response r) {
                    ToastPersonalizado.mostrarErrorApi(PerfilActivity.this, r);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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