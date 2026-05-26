package es.pmdm.tikitaka_app;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    public static final String PREFS_NAME = "TikiTakaPrefs";

    // Claves
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_USUARIO_ID = "usuarioId";
    public static final String KEY_NOMBRE_USUARIO = "nombreUsuario";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_EQUIPO_FAVORITO  = "equipoFavoritoId";


    // Guardar sesión tras login/registro
    public static void guardarSesion(Context context, String token,
                                     long usuarioId, String nombreUsuario,
                                     String email, long equipoFavoritoId) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_TOKEN, token);
        editor.putLong(KEY_USUARIO_ID, usuarioId);
        editor.putString(KEY_NOMBRE_USUARIO, nombreUsuario);
        editor.putString(KEY_EMAIL, email);
        editor.putLong(KEY_EQUIPO_FAVORITO, equipoFavoritoId);
        editor.apply();
    }
    public static boolean isLoggedIn(Context context) {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public static String getToken(Context context) {
        return getPrefs(context).getString(KEY_TOKEN, null);
    }

    public static long getUsuarioId(Context context) {
        return getPrefs(context).getLong(KEY_USUARIO_ID, -1);
    }

    public static String getNombreUsuario(Context context) {
        return getPrefs(context).getString(KEY_NOMBRE_USUARIO, "");
    }

    public static String getEmail(Context context) {
        return getPrefs(context).getString(KEY_EMAIL, "");
    }

    public static long getEquipoFavoritoId(Context context) {
        return getPrefs(context).getLong(KEY_EQUIPO_FAVORITO, -1);
    }

    // Actualizar equipo favorito (desde PerfilActivity)
    public static void actualizarEquipoFavorito(Context context, long equipoId) {
        getPrefs(context).edit()
                .putLong(KEY_EQUIPO_FAVORITO, equipoId)
                .apply();
    }

    // Cerrar sesión — borra todo
    public static void cerrarSesion(Context context) {
        getPrefs(context).edit().clear().apply();
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}