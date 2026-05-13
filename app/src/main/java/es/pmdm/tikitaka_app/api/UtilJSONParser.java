package es.pmdm.tikitaka_app.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.pmdm.tikitaka_app.modelos.Alineacion;
import es.pmdm.tikitaka_app.modelos.Competicion;
import es.pmdm.tikitaka_app.modelos.Equipo;
import es.pmdm.tikitaka_app.modelos.Estadistica;
import es.pmdm.tikitaka_app.modelos.Gol;
import es.pmdm.tikitaka_app.modelos.Jornada;
import es.pmdm.tikitaka_app.modelos.Jugador;
import es.pmdm.tikitaka_app.modelos.Noticia;
import es.pmdm.tikitaka_app.modelos.Notificacion;
import es.pmdm.tikitaka_app.modelos.Partido;
import es.pmdm.tikitaka_app.modelos.RankingItem;
import es.pmdm.tikitaka_app.modelos.Tarjeta;
import es.pmdm.tikitaka_app.modelos.Usuario;

public class UtilJSONParser {

    // PARTIDO
    public static List<Partido> parseArrayPartidos(String strJson) {
        List<Partido> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(strJson);
            for (int i = 0; i < array.length(); i++) {
                list.add(parsePartido(array.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Partido parsePartido(String strJson) {
        Partido partido = new Partido();
        try {
            JSONObject json = new JSONObject(strJson);
            partido.setId(json.optLong("id", -1));
            partido.setEquipoLocalId(json.optLong("equipoLocalId", -1));
            partido.setEquipoVisitanteId(json.optLong("equipoVisitanteId", -1));
            partido.setJornadaId(json.optLong("jornadaId", -1));
            partido.setFechaHora(json.optString("fechaHora", ""));
            partido.setEstado(json.optString("estado", ""));
            partido.setGolesLocal(json.optInt("golesLocal", 0));
            partido.setGolesVisitante(json.optInt("golesVisitante", 0));
            partido.setMinutoActual(json.optInt("minutoActual", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return partido;
    }

    public static JSONObject createPartido(Long equipoLocalId, Long equipoVisitanteId,
                                           Long jornadaId, String fechaHora, String estado) {
        JSONObject json = new JSONObject();
        try {
            json.put("equipoLocalId", equipoLocalId);
            json.put("equipoVisitanteId", equipoVisitanteId);
            json.put("jornadaId", jornadaId);
            json.put("fechaHora", fechaHora);
            json.put("estado", estado);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    // EQUIPO
    public static List<Equipo> parseArrayEquipos(String strJson) {
        List<Equipo> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(strJson);
            for (int i = 0; i < array.length(); i++) {
                list.add(parseEquipo(array.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Equipo parseEquipo(String strJson) {
        Equipo equipo = new Equipo();
        try {
            JSONObject json = new JSONObject(strJson);
            equipo.setId(json.optLong("id", -1));
            equipo.setNombre(json.optString("nombre", ""));
            equipo.setCiudad(json.optString("ciudad", ""));
            equipo.setEstadio(json.optString("estadio", ""));
            equipo.setEscudoUrl(json.optString("escudoUrl", ""));
            equipo.setEntrenador(json.optString("entrenador", ""));
            equipo.setAnioFundacion(json.optInt("anioFundacion", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return equipo;
    }

    // USUARIO
    public static Usuario parseUsuario(String strJson) {
        Usuario usuario = new Usuario();
        try {
            JSONObject json = new JSONObject(strJson);
            usuario.setId(json.optLong("id", -1));
            usuario.setNombreUsuario(json.optString("nombreUsuario", ""));
            usuario.setEmail(json.optString("email", ""));
            usuario.setEquipoFavoritoId(json.optLong("equipoFavoritoId", -1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    public static JSONObject createUsuario(String nombreUsuario, String email,
                                           String contrasena, Long equipoFavoritoId) {
        JSONObject json = new JSONObject();
        try {
            json.put("nombreUsuario", nombreUsuario);
            json.put("email", email);
            json.put("contrasena", contrasena);
            json.put("equipoFavoritoId", equipoFavoritoId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static JSONObject createLogin(String nombreUsuario, String contrasena) {
        JSONObject json = new JSONObject();
        try {
            json.put("nombreUsuario", nombreUsuario);
            json.put("contrasena", contrasena);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    // NOTICIA
    public static List<Noticia> parseArrayNoticias(String strJson) {
        List<Noticia> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(strJson);
            for (int i = 0; i < array.length(); i++) {
                list.add(parseNoticia(array.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Noticia parseNoticia(String strJson) {
        Noticia noticia = new Noticia();
        try {
            JSONObject json = new JSONObject(strJson);
            noticia.setId(json.optLong("id", -1));
            noticia.setTitulo(json.optString("titulo", ""));
            noticia.setContenido(json.optString("contenido", ""));
            noticia.setFechaPublicacion(json.optString("fechaPublicacion", ""));
            noticia.setEquipoId(json.optLong("equipoId", -1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return noticia;
    }

    // ESTADISTICA
    public static Estadistica parseEstadistica(String strJson) {
        Estadistica estadistica = new Estadistica();
        try {
            JSONObject json = new JSONObject(strJson);
            estadistica.setId(json.optLong("id", -1));
            estadistica.setPartidoId(json.optLong("partidoId", -1));
            estadistica.setPosesionLocal(json.optInt("posesionLocal", 0));
            estadistica.setPosesionVisitante(json.optInt("posesionVisitante", 0));
            estadistica.setTirosLocal(json.optInt("tirosLocal", 0));
            estadistica.setTirosVisitante(json.optInt("tirosVisitante", 0));
            estadistica.setTirosAPuertaLocal(json.optInt("tirosAPuertaLocal", 0));
            estadistica.setTirosAPuertaVisitante(json.optInt("tirosAPuertaVisitante", 0));
            estadistica.setCornersLocal(json.optInt("cornersLocal", 0));
            estadistica.setCornersVisitante(json.optInt("cornersVisitante", 0));
            estadistica.setFaltasLocal(json.optInt("faltasLocal", 0));
            estadistica.setFaltasVisitante(json.optInt("faltasVisitante", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return estadistica;
    }

    // GOL
    public static List<Gol> parseArrayGoles(String strJson) {
        List<Gol> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(strJson);
            for (int i = 0; i < array.length(); i++) {
                list.add(parseGol(array.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Gol parseGol(String strJson) {
        Gol gol = new Gol();
        try {
            JSONObject json = new JSONObject(strJson);
            gol.setId(json.optLong("id", -1));
            gol.setPartidoId(json.optLong("partidoId", -1));
            gol.setJugadorId(json.optLong("jugadorId", -1));
            gol.setMinuto(json.optInt("minuto", 0));
            gol.setTipo(json.optString("tipo", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gol;
    }

    // TARJETA
    public static List<Tarjeta> parseArrayTarjetas(String strJson) {
        List<Tarjeta> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(strJson);
            for (int i = 0; i < array.length(); i++) {
                list.add(parseTarjeta(array.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Tarjeta parseTarjeta(String strJson) {
        Tarjeta tarjeta = new Tarjeta();
        try {
            JSONObject json = new JSONObject(strJson);
            tarjeta.setId(json.optLong("id", -1));
            tarjeta.setPartidoId(json.optLong("partidoId", -1));
            tarjeta.setJugadorId(json.optLong("jugadorId", -1));
            tarjeta.setMinuto(json.optInt("minuto", 0));
            tarjeta.setTipo(json.optString("tipo", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tarjeta;
    }

    // JUGADOR
    public static List<Jugador> parseArrayJugadores(String strJson) {
        List<Jugador> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(strJson);
            for (int i = 0; i < array.length(); i++) {
                list.add(parseJugador(array.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Jugador parseJugador(String strJson) {
        Jugador jugador = new Jugador();
        try {
            JSONObject json = new JSONObject(strJson);
            jugador.setId(json.optLong("id", -1));
            jugador.setNombre(json.optString("nombre", ""));
            jugador.setApellidos(json.optString("apellidos", ""));
            jugador.setPosicion(json.optString("posicion", ""));
            jugador.setDorsal(json.optInt("dorsal", 0));
            jugador.setNacionalidad(json.optString("nacionalidad", ""));
            jugador.setEquipoId(json.optLong("equipoId", -1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jugador;
    }

    // ALINEACION
    public static List<Alineacion> parseArrayAlineaciones(String strJson) {
        List<Alineacion> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(strJson);
            for (int i = 0; i < array.length(); i++) {
                list.add(parseAlineacion(array.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Alineacion parseAlineacion(String strJson) {
        Alineacion alineacion = new Alineacion();
        try {
            JSONObject json = new JSONObject(strJson);
            alineacion.setId(json.optLong("id", -1));
            alineacion.setPartidoId(json.optLong("partidoId", -1));
            alineacion.setJugadorId(json.optLong("jugadorId", -1));
            alineacion.setTitular(json.optBoolean("titular", false));
            alineacion.setMinutoEntrada(json.optInt("minutoEntrada", 0));
            alineacion.setMinutoSalida(json.optInt("minutoSalida", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return alineacion;
    }

    // JORNADA
    public static List<Jornada> parseArrayJornadas(String strJson) {
        List<Jornada> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(strJson);
            for (int i = 0; i < array.length(); i++) {
                list.add(parseJornada(array.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Jornada parseJornada(String strJson) {
        Jornada jornada = new Jornada();
        try {
            JSONObject json = new JSONObject(strJson);
            jornada.setId(json.optLong("id", -1));
            jornada.setNumero(json.optInt("numero", 0));
            jornada.setCompeticionId(json.optLong("competicionId", -1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jornada;
    }

    // COMPETICION
    public static List<Competicion> parseArrayCompeticiones(String strJson) {
        List<Competicion> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(strJson);
            for (int i = 0; i < array.length(); i++) {
                list.add(parseCompeticion(array.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Competicion parseCompeticion(String strJson) {
        Competicion competicion = new Competicion();
        try {
            JSONObject json = new JSONObject(strJson);
            competicion.setId(json.optLong("id", -1));
            competicion.setNombre(json.optString("nombre", ""));
            competicion.setTemporada(json.optString("temporada", ""));
            competicion.setLogoUrl(json.optString("logoUrl", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return competicion;
    }

    // NOTIFICACION
    public static List<Notificacion> parseArrayNotificaciones(String strJson) {
        List<Notificacion> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(strJson);
            for (int i = 0; i < array.length(); i++) {
                list.add(parseNotificacion(array.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Notificacion parseNotificacion(String strJson) {
        Notificacion notificacion = new Notificacion();
        try {
            JSONObject json = new JSONObject(strJson);
            notificacion.setId(json.optLong("id", -1));
            notificacion.setUsuarioId(json.optLong("usuarioId", -1));
            notificacion.setPartidoId(json.optLong("partidoId", -1));
            notificacion.setMensaje(json.optString("mensaje", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return notificacion;
    }

    // GOLEADORES
    public static List<RankingItem> parseGoleadores(String strJson) {
        List<RankingItem> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(strJson);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                String nombre = json.optString("nombre", "") + " " + json.optString("apellidos", "");
                int total = (int) json.optLong("total", 0);
                list.add(new RankingItem(nombre.trim(), total));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    //MAS TARJETAS
    public static List<RankingItem> parseMasTarjetas(String strJson) {
        List<RankingItem> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(strJson);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                String nombre = json.optString("nombre", "") + " " + json.optString("apellidos", "");
                int total = (int) json.optLong("total", 0);
                list.add(new RankingItem(nombre.trim(), total));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    private UtilJSONParser() { throw new AssertionError(); }
}