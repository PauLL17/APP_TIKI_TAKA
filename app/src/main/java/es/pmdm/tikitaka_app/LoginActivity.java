package es.pmdm.tikitaka_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGuest;
    private TextView tvRegister;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("TikiTakaPrefs", MODE_PRIVATE);

        // Verificar si ya hay sesión iniciada
        if (isUserLoggedIn()) {
            navigateToMain();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGuest = findViewById(R.id.btnGuest);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        btnGuest.setOnClickListener(v -> loginAsGuest());

        tvRegister.setOnClickListener(v -> {
            Toast.makeText(this, "Registro aún no implementado", Toast.LENGTH_SHORT).show();
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        // Por ahora, login simulado con datos de prueba
        if (email.equals("admin@tikitaka.com") && password.equals("admin123")) {
            saveUserSession(email, "Administrador", "administrador");
            Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else if (password.equals("pass123")) {
            // Cualquier email con esta contraseña funciona
            saveUserSession(email, "Usuario", "registrado");
            Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void loginAsGuest() {
        saveUserSession("invitado@tikitaka.com", "Invitado", "invitado");
        navigateToMain();
    }

    private void saveUserSession(String email, String nombre, String tipo) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userEmail", email);
        editor.putString("userName", nombre);
        editor.putString("userType", tipo);
        editor.apply();
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}