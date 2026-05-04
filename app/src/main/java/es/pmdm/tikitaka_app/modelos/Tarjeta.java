package es.pmdm.tikitaka_app.modelos;

import java.io.Serializable;

public class Tarjeta implements Serializable {
    public static final String TIPO_AMARILLA = "AMARILLA";
    public static final String TIPO_ROJA     = "ROJA";

    private Long id;
    private Long partidoId;
    private Long jugadorId;
    private Integer minuto;
    private String tipo;

    private Jugador jugador;

    public Tarjeta() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    public Long getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(Long jugadorId) {
        this.jugadorId = jugadorId;
    }

    public Integer getMinuto() {
        return minuto != null ? minuto : 0;
    }

    public void setMinuto(Integer minuto) {
        this.minuto = minuto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }
}
