package es.pmdm.tikitaka_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences(
                SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        String idiomaDispositivo = Locale.getDefault().getLanguage();
        String idioma = prefs.getString(SettingsActivity.KEY_IDIOMA, idiomaDispositivo);

        Locale locale = new Locale(idioma);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);
        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }
}