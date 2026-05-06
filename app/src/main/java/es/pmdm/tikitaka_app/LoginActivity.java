package es.pmdm.tikitaka_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import es.pmdm.tikitaka_app.api.API;
import es.pmdm.tikitaka_app.api.UtilJSONParser;
import es.pmdm.tikitaka_app.api.UtilREST;
import es.pmdm.tikitaka_app.modelos.Usuario;

public class LoginActivity extends AppCompatActivity {
    private EditText etNombreUsuario, etPassword;
    private Button btnLogin, btnGuest;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (SessionManager.isLoggedIn(this)) {
            navigateToMain();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etNombreUsuario = findViewById(R.id.etEmail);
        etPassword      = findViewById(R.id.etPassword);
        btnLogin        = findViewById(R.id.btnLogin);
        btnGuest        = findViewById(R.id.btnGuest);
        tvRegister      = findViewById(R.id.tvRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        btnGuest.setOnClickListener(v -> loginAsGuest());

        tvRegister.setOnClickListener(v -> {
            ToastPersonalizado.mostrarCorto(this, getString(R.string.coming_soon));
        });
    }

    private void attemptLogin() {
        String nombreUsuario = etNombreUsuario.getText().toString().trim();
        String password      = etPassword.getText().toString().trim();

        if (nombreUsuario.isEmpty() || password.isEmpty()) {
            ToastPersonalizado.mostrarError(this, getString(R.string.empty_fields));
            return;
        }

        JSONObject body = UtilJSONParser.createLogin(nombreUsuario, password);

        API.login(body, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                // La API devuelve el token directamente como string
                String token = r.content;

                // Con el token obtenemos los datos del usuario
                API.getUsuarioByUsername(nombreUsuario, token, new UtilREST.OnResponseListener() {
                    @Override
                    public void onSuccess(UtilREST.Response r2) {
                        Usuario usuario = UtilJSONParser.parseUsuario(r2.content);
                        SessionManager.guardarSesion(
                                LoginActivity.this,
                                token,
                                usuario.getId(),
                                usuario.getNombreUsuario(),
                                usuario.getEmail(),
                                usuario.getEquipoFavoritoId()
                        );
                        ToastPersonalizado.mostrarCorto(LoginActivity.this, getString(R.string.login_success));
                        navigateToMain();
                    }

                    @Override
                    public void onError(UtilREST.Response r2) {
                        ToastPersonalizado.mostrarError(LoginActivity.this, getString(R.string.login_error));
                    }
                });
            }

            @Override
            public void onError(UtilREST.Response r) {
                ToastPersonalizado.mostrarError(LoginActivity.this, getString(R.string.login_error));
            }
        });
    }

    private void loginAsGuest() {
        SessionManager.guardarSesionInvitado(this);
        navigateToMain();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}