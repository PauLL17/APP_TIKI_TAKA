package es.pmdm.tikitaka_app.api;

import org.json.JSONObject;

// Proporciona métodos para interactuar con el API REST de Tiki-Taka.
public class API {
    private static final String BASE_URL = "http://192.168.10.231:8080/";
    //private static final String BASE_URL = "http://10.0.2.2:8080/";
    // private static final String BASE_URL = "http://52.201.180.205/";

    // AUTH
    public static void login(JSONObject body, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.POST, BASE_URL + "auth/login", body.toString(), listener);
    }

    public static void register(JSONObject body, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.POST, BASE_URL + "auth/register", body.toString(), listener);
    }

    // PARTIDOS
    public static void getPartidos(String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/partidos", null, token, listener);
    }

    public static void getPartido(long id, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/partidos/" + id, null, token, listener);
    }

    public static void postPartido(JSONObject body, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.POST, BASE_URL + "api/partidos", body.toString(), token, listener);
    }

    public static void putPartido(long id, JSONObject body, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.PUT, BASE_URL + "api/partidos", body.toString(), token, listener);
    }

    public static void deletePartido(long id, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.DELETE, BASE_URL + "api/partidos/" + id, null, token, listener);
    }

    // EQUIPOS
    public static void getEquipos(String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/equipos", null, token, listener);
    }

    public static void getEquipo(long id, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/equipos/" + id, null, token, listener);
    }

    public static void postEquipo(JSONObject body, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.POST, BASE_URL + "api/equipos", body.toString(), token, listener);
    }

    public static void putEquipo(long id, JSONObject body, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.PUT, BASE_URL + "api/equipos", body.toString(), token, listener);
    }

    public static void deleteEquipo(long id, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.DELETE, BASE_URL + "api/equipos/" + id, null, token, listener);
    }

    // JUGADORES
    public static void getJugador(long id, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/jugadores/" + id, null, token, listener);
    }

    public static void getJugadoresByEquipo(long equipoId, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/jugadores/equipo/" + equipoId, null, token, listener);
    }

    public static void getJugadoresByEquipoYPosicion(long equipoId, String posicion, String token, UtilREST.OnResponseListener listener) { //Consulta JPA
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/jugadores/equipo/" + equipoId + "/posicion/" + posicion, null, token, listener);
    }

    public static void postJugador(JSONObject body, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.POST, BASE_URL + "api/jugadores", body.toString(), token, listener);
    }

    public static void putJugador(long id, JSONObject body, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.PUT, BASE_URL + "api/jugadores", body.toString(), token, listener);
    }
    public static void deleteJugador(long id, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.DELETE, BASE_URL + "api/jugadores/" + id, null, token, listener);
    }

    // GOLES
    public static void getGolesByPartido(long partidoId, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/goles/partido/" + partidoId, null, token, listener);
    }

    public static void getGolesByJugador(long jugadorId, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/goles/jugador/" + jugadorId, null, token, listener);
    }

    public static void getGoleadores(String token, UtilREST.OnResponseListener listener) { //Consulta JPA
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/goles/goleadores", null, token, listener);
    }

    public static void postGol(JSONObject body, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.POST, BASE_URL + "api/goles", body.toString(), token, listener);
    }

    // TARJETAS
    public static void getTarjetasByPartido(long partidoId, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/tarjetas/partido/" + partidoId, null, token, listener);
    }

    public static void getTarjetasByJugador(long jugadorId, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/tarjetas/jugador/" + jugadorId, null, token, listener);
    }

    public static void getMasTarjetas(String token, UtilREST.OnResponseListener listener) { //Consulta JPA
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/tarjetas/mas-tarjetas", null, token, listener);
    }

    // ALINEACIONES
    public static void getTitularesByPartido(long partidoId, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/alineaciones/partido/" + partidoId + "/titulares", null, token, listener);
    }

    public static void postAlineacion(JSONObject body, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.POST, BASE_URL + "api/alineaciones", body.toString(), token, listener);
    }

    // ESTADISTICAS
    public static void getEstadisticasByPartido(long partidoId, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/estadisticas/partido/" + partidoId, null, token, listener);
    }

    public static void postEstadistica(JSONObject body, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.POST, BASE_URL + "api/estadisticas", body.toString(), token, listener);
    }

    public static void putEstadistica(JSONObject body, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.PUT, BASE_URL + "api/estadisticas", body.toString(), token, listener);
    }

    // JORNADAS
    public static void getJornadas(String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/jornadas", null, token, listener);
    }

    // NOTICIAS
    public static void getNoticia(long id, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/noticias/" + id, null, token, listener);
    }

    public static void getNoticiasByEquipo(long equipoId, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/noticias/equipo/" + equipoId, null, token, listener);
    }

    public static void getUltimasNoticias(String token, UtilREST.OnResponseListener listener) { //Consulta JPA
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/noticias/ultimas", null, token, listener);
    }

    public static void deleteNoticia(long id, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.DELETE, BASE_URL + "api/noticias/" + id, null, token, listener);
    }

    public static void postNoticia(JSONObject body, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.POST, BASE_URL + "api/noticias", body.toString(), token, listener);
    }

    //Tarjetas
    public static void postTarjeta(JSONObject body, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.POST, BASE_URL + "api/tarjetas", body.toString(), token, listener);
    }

    // USUARIOS
    public static void getUsuarioByUsername(String nombreUsuario, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/usuarios/username/" + nombreUsuario, null, token, listener);
    }

    public static void putUsuario(long id, JSONObject body, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.PUT, BASE_URL + "api/usuarios", body.toString(), token, listener);
    }

    // JOOQ
    public static void getJugadoresByEquipoJooq(long equipoId, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/jooq/jugadores/equipo/" + equipoId, null, token, listener);
    }

    public static void buscarJugadoresJooq(String nombre, String posicion, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/jooq/jugadores/busqueda?nombre=" + nombre + "&posicion=" + posicion, null, token, listener);
    }

    public static void getPartidosMasGolesJooq(String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.GET, BASE_URL + "api/jooq/partidos/mas-goles", null, token, listener);
    }

    private API() { throw new AssertionError(); }
}