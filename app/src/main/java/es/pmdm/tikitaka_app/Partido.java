package es.pmdm.tikitaka_app;

import java.io.Serializable;

public class Partido implements Serializable {

    public static final String ESTADO_EN_VIVO    = "EN_VIVO";
    public static final String ESTADO_PROGRAMADO = "PROGRAMADO";
    public static final String ESTADO_FINALIZADO = "FINALIZADO";

    private Long id;
    private Long equipoLocalId;
    private Long equipoVisitanteId;
    private Long jornadaId;
    private String fechaHora;
    private String estado;
    private Integer golesLocal;
    private Integer golesVisitante;
    private Integer minutoActual;

    // Resueltos en el cliente
    private Equipo equipoLocal;
    private Equipo equipoVisitante;

    public Partido() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquipoLocalId() {
        return equipoLocalId;
    }
    public void setEquipoLocalId(Long equipoLocalId) {
        this.equipoLocalId = equipoLocalId;
    }

    public Long getEquipoVisitanteId() {
        return equipoVisitanteId;
    }
    public void setEquipoVisitanteId(Long equipoVisitanteId) {
        this.equipoVisitanteId = equipoVisitanteId;
    }

    public Long getJornadaId() {
        return jornadaId;
    }
    public void setJornadaId(Long jornadaId) {
        this.jornadaId = jornadaId;
    }

    public String getFechaHora() {
        return fechaHora;
    }
    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getGolesLocal() {
        return golesLocal != null ? golesLocal : 0;
    }
    public void setGolesLocal(Integer golesLocal) {
        this.golesLocal = golesLocal;
    }

    public Integer getGolesVisitante() {
        return golesVisitante != null ? golesVisitante : 0;
    }
    public void setGolesVisitante(Integer golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    public Integer getMinutoActual() {
        return minutoActual != null ? minutoActual : 0;
    }
    public void setMinutoActual(Integer minutoActual) {
        this.minutoActual = minutoActual;
    }

    public Equipo getEquipoLocal() {
        return equipoLocal;
    }
    public void setEquipoLocal(Equipo equipoLocal) {
        this.equipoLocal = equipoLocal;
    }

    public Equipo getEquipoVisitante() {
        return equipoVisitante;
    }
    public void setEquipoVisitante(Equipo equipoVisitante) {
        this.equipoVisitante = equipoVisitante;
    }

    public boolean isEnVivo() {
        return ESTADO_EN_VIVO.equals(estado);
    }
    public boolean isProgramado() {
        return ESTADO_PROGRAMADO.equals(estado);
    }
    public boolean isFinalizado() {
        return ESTADO_FINALIZADO.equals(estado);
    }
}