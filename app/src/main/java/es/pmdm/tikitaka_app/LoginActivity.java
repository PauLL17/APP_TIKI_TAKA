package es.pmdm.tikitaka_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGuest;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Si ya hay sesión activa saltamos directamente a MainActivity
        if (SessionManager.isLoggedIn(this)) {
            navigateToMain();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        btnGuest   = findViewById(R.id.btnGuest);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        btnGuest.setOnClickListener(v -> loginAsGuest());

        tvRegister.setOnClickListener(v -> {
            ToastPersonalizado.mostrarCorto(this, "Registro próximamente");
        });
    }

    private void attemptLogin() {
        String nombreUsuario = etEmail.getText().toString().trim();
        String password      = etPassword.getText().toString().trim();

        if (nombreUsuario.isEmpty() || password.isEmpty()) {
            ToastPersonalizado.mostrarCorto(this, getString(R.string.empty_fields));
            return;
        }

        if (nombreUsuario.equals("admin") && password.equals("admin1234")) {
            SessionManager.guardarSesion(this, "token_mock", 1L,
                    "admin", "admin@tikitaka.com", 1L);
            ToastPersonalizado.mostrarCorto(this, getString(R.string.login_success));
            navigateToMain();
        } else {
            ToastPersonalizado.mostrarCorto(this, getString(R.string.login_error));
        }
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