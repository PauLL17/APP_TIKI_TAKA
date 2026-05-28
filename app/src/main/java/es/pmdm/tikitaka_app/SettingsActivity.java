package es.pmdm.tikitaka_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

import es.pmdm.tikitaka_app.utilidades.BaseActivity;
import es.pmdm.tikitaka_app.utilidades.ToastPersonalizado;

public class SettingsActivity extends BaseActivity {

    private CheckBox checkNotificaciones;
    private RadioGroup radioGroupIdioma;
    private RadioButton radioEspanol, radioIngles;
    private SharedPreferences preferences;

    public static final String PREFS_NAME     = "TikiTakaSettings";
    public static final String KEY_NOTIF      = "notificaciones_activas";
    public static final String KEY_IDIOMA     = "idioma";
    public static final String IDIOMA_ES      = "es";
    public static final String IDIOMA_EN      = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initViews();
        setupToolbar();
        cargarPreferencias();
        setupListeners();
    }

    private void initViews() {
        checkNotificaciones = findViewById(R.id.checkNotificaciones);
        radioGroupIdioma    = findViewById(R.id.radioGroupIdioma);
        radioEspanol        = findViewById(R.id.radioEspanol);
        radioIngles         = findViewById(R.id.radioIngles);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.ajustes);
        }
    }

    private void cargarPreferencias() {
        boolean notifActivas = preferences.getBoolean(KEY_NOTIF, true);
        checkNotificaciones.setChecked(notifActivas);

        String idiomaDispositivo = Locale.getDefault().getLanguage();
        String idioma = preferences.getString(KEY_IDIOMA, idiomaDispositivo);

        if (idioma.equals(IDIOMA_EN)) {
            radioIngles.setChecked(true);
        } else {
            radioEspanol.setChecked(true);
        }
    }

    private void setupListeners() {
        checkNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_NOTIF, isChecked);
            editor.apply();
        });

        radioGroupIdioma.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = preferences.edit();
            if (checkedId == R.id.radioIngles) {
                editor.putString(KEY_IDIOMA, IDIOMA_EN);
            } else {
                editor.putString(KEY_IDIOMA, IDIOMA_ES);
            }
            editor.apply();
            ToastPersonalizado.mostrarCorto(this, getString(R.string.reiniciar_app));
        });
    }

    public static boolean notificacionesActivas(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_NOTIF, true);
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