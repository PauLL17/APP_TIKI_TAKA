package es.pmdm.tikitaka_app;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import es.pmdm.tikitaka_app.api.UtilREST;

public class ToastPersonalizado {

    public static void mostrar(Context context, String mensaje, int duracion) {
        View toastLayout = LayoutInflater.from(context)
                .inflate(R.layout.toast_personalizado, null);

        TextView textView = toastLayout.findViewById(R.id.toastMessage);
        textView.setText(mensaje);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.setDuration(duracion);
        toast.setView(toastLayout);
        toast.show();
    }

    public static void mostrarCorto(Context context, String mensaje) {
        mostrar(context, mensaje, Toast.LENGTH_SHORT);
    }

    public static void mostrarLargo(Context context, String mensaje) {
        mostrar(context, mensaje, Toast.LENGTH_LONG);
    }

    public static void mostrarError(Context context, String mensaje) {
        mostrar(context, mensaje, Toast.LENGTH_SHORT);
    }

    public static void mostrarErrorApi(Context context, UtilREST.Response response) {
        String mensaje;
        try {
            JSONObject json = new JSONObject(response.content);
            mensaje = json.optString("message", context.getString(R.string.error_cargar_datos));
        } catch (Exception e) {
            if (response.responseCode == 403) {
                mensaje = context.getString(R.string.error_sin_permisos);
            } else if (response.responseCode == 401) {
                mensaje = context.getString(R.string.error_sesion_expirada);
            } else {
                mensaje = context.getString(R.string.error_cargar_datos);
            }
        }
        mostrar(context, mensaje, Toast.LENGTH_LONG);
    }
}
