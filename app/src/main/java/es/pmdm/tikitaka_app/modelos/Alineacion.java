package es.pmdm.tikitaka_app.modelos;

import java.io.Serializable;

public class Alineacion implements Serializable {
    private Long id;
    private Long partidoId;
    private Long jugadorId;
    private Boolean titular;
    private Integer minutoEntrada;
    private Integer minutoSalida;

    private Jugador jugador;

    public Alineacion() {}

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

    public Boolean isTitular() {
        return titular != null && titular;
    }

    public void setTitular(Boolean titular) {
        this.titular = titular;
    }

    public Integer getMinutoEntrada() {
        return minutoEntrada;
    }

    public void setMinutoEntrada(Integer minutoEntrada) {
        this.minutoEntrada = minutoEntrada;
    }

    public Integer getMinutoSalida() {
        return minutoSalida;
    }

    public void setMinutoSalida(Integer minutoSalida) {
        this.minutoSalida = minutoSalida;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }
}
