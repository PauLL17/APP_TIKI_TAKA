package es.pmdm.tikitaka_app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {

    private static WebSocketManager instance;
    private OkHttpClient client;
    private WebSocket webSocketNotificaciones;
    private WebSocket webSocketMarcador;
    private Context context;

    public interface MarcadorListener {
        void onMarcadorActualizado(int golesLocal, int golesVisitante, String estado);
    }

    private WebSocketManager() {
        client = new OkHttpClient();
    }

    public static WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void conectarNotificaciones(Context context, long usuarioId) {
        this.context = context.getApplicationContext();

        String url = "ws://" + getHost() + "/ws/notificaciones/" + usuarioId;

        Request request = new Request.Builder()
                .url(url)
                .build();

        webSocketNotificaciones = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                android.util.Log.d("WebSocket", "Notificaciones conectado");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                android.util.Log.d("WebSocket", "Notificacion recibida: " + text);
                mostrarNotificacion(text);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                android.util.Log.e("WebSocket", "Error notificaciones: " + t.getMessage());
            }
        });
    }

    public void conectarMarcador(long partidoId, MarcadorListener listener) {
        String url = "ws://" + getHost() + "/ws/partido/" + partidoId;

        Request request = new Request.Builder()
                .url(url)
                .build();

        webSocketMarcador = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                android.util.Log.d("WebSocket", "Marcador conectado");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                android.util.Log.d("WebSocket", "Marcador actualizado: " + text);
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        org.json.JSONObject json = new org.json.JSONObject(text);
                        int golesLocal     = json.optInt("golesLocal", 0);
                        int golesVisitante = json.optInt("golesVisitante", 0);
                        String estado      = json.optString("estado", "");
                        listener.onMarcadorActualizado(golesLocal, golesVisitante, estado);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                android.util.Log.e("WebSocket", "Error marcador: " + t.getMessage());
            }
        });
    }

    private void mostrarNotificacion(String texto) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                org.json.JSONObject json = new org.json.JSONObject(texto);
                String mensaje = json.optString("mensaje", texto);
                new NotificationHelper(context).mostrarNotificacionExpandida(
                        "TikiTaka",
                        mensaje,
                        NotificationHelper.NOTIF_GOL
                );
            } catch (Exception e) {
                new NotificationHelper(context).mostrarNotificacionExpandida(
                        "TikiTaka",
                        texto,
                        NotificationHelper.NOTIF_GOL
                );
            }
        });
    }

    private String getHost() {
        return "192.168.10.231:8080";
        //return "10.0.2.2:8080";
        //return "52.201.180.205";
    }

    public void desconectarNotificaciones() {
        if (webSocketNotificaciones != null) {
            webSocketNotificaciones.close(1000, "Cierre de sesión");
            webSocketNotificaciones = null;
        }
    }

    public void desconectarMarcador() {
        if (webSocketMarcador != null) {
            webSocketMarcador.close(1000, "Saliendo del partido");
            webSocketMarcador = null;
        }
    }

    public void desconectarTodo() {
        desconectarNotificaciones();
        desconectarMarcador();
    }
}
