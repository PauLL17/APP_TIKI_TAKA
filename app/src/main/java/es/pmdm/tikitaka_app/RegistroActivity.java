package es.pmdm.tikitaka_app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.pmdm.tikitaka_app.api.API;
import es.pmdm.tikitaka_app.api.UtilJSONParser;
import es.pmdm.tikitaka_app.api.UtilREST;
import es.pmdm.tikitaka_app.modelos.Equipo;
import es.pmdm.tikitaka_app.utilidades.BaseActivity;
import es.pmdm.tikitaka_app.utilidades.ToastPersonalizado;

public class RegistroActivity extends BaseActivity {
    private EditText etNombreUsuario, etEmail, etPassword;
    private Spinner spinnerEquipo;
    private Button btnRegister;
    private TextView tvLogin;

    private List<Equipo> listaEquipos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        initViews();
        setupListeners();
        cargarEquipos();
    }

    private void initViews() {
        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etEmail         = findViewById(R.id.etEmail);
        etPassword      = findViewById(R.id.etPassword);
        spinnerEquipo   = findViewById(R.id.spinnerEquipo);
        btnRegister     = findViewById(R.id.btnRegister);
        tvLogin         = findViewById(R.id.tvLogin);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());

        tvLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void cargarEquipos() {
        // Cargamos los equipos sin token porque el registro es libre
        API.getEquipos(null, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                listaEquipos = UtilJSONParser.parseArrayEquipos(r.content);

                List<String> nombresEquipos = new ArrayList<>();
                for (Equipo equipo : listaEquipos)
                {
                    nombresEquipos.add(equipo.getNombre());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        RegistroActivity.this,
                        android.R.layout.simple_spinner_item,
                        nombresEquipos
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEquipo.setAdapter(adapter);
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarError(RegistroActivity.this, getString(R.string.register_error));
            }
        });
    }

    private void attemptRegister() {
        String nombreUsuario = etNombreUsuario.getText().toString().trim();
        String email         = etEmail.getText().toString().trim();
        String password      = etPassword.getText().toString().trim();

        if (nombreUsuario.isEmpty() || email.isEmpty() || password.isEmpty()) {
            ToastPersonalizado.mostrarError(this, getString(R.string.empty_fields));
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ToastPersonalizado.mostrarError(this, getString(R.string.error_email_invalido));
            return;
        }

        if (password.length() < 6) {
            ToastPersonalizado.mostrarError(this, getString(R.string.error_password_corta));
            return;
        }

        if (listaEquipos.isEmpty()) {
            ToastPersonalizado.mostrarError(this, getString(R.string.register_error));
            return;
        }

        Equipo equipoSeleccionado = listaEquipos.get(spinnerEquipo.getSelectedItemPosition());

        JSONObject body = UtilJSONParser.createUsuario(
                nombreUsuario,
                email,
                password,
                equipoSeleccionado.getId()
        );

        API.register(body, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                ToastPersonalizado.mostrarCorto(RegistroActivity.this, getString(R.string.register_success));
                finish();
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarError(RegistroActivity.this, getString(R.string.register_error));
            }
        });
    }
}