package es.pmdm.tikitaka_app.utilidades;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import es.pmdm.tikitaka_app.SettingsActivity;

public class NotificationHelper {

    private static final String CANAL_ID = "canal_notificaciones_TikiTaka";
    public static final int NOTIF_CREAR    = 1;
    public static final int NOTIF_ELIMINAR = 2;
    public static final int NOTIF_EDITAR   = 3;
    public static final int NOTIF_GOL      = 4;
    public static final int NOTIF_NOTICIA  = 5;

    private final Context context;
    private NotificationManager manager;

    public NotificationHelper(Context context) {
        this.context = context;
        crearCanalNotificacion();
    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CANAL_ID,
                    "TikiTaka",
                    NotificationManager.IMPORTANCE_HIGH
            );
            canal.setDescription("Notificaciones de TikiTaka App");
            manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(canal);
        }
    }

    public void mostrarNotificacion(String titulo, String mensaje, int notificationId) {
        if (!SettingsActivity.notificacionesActivas(context)) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, builder.build());
    }

    public void mostrarNotificacionExpandida(String titulo, String mensaje, int notificationId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, builder.build());
    }
}