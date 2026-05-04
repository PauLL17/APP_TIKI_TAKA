package es.pmdm.tikitaka_app.modelos;

import java.io.Serializable;

public class Estadistica implements Serializable {
    private Long id;
    private Long partidoId;
    private Integer posesionLocal;
    private Integer posesionVisitante;
    private Integer tirosLocal;
    private Integer tirosVisitante;
    private Integer tirosAPuertaLocal;
    private Integer tirosAPuertaVisitante;
    private Integer cornersLocal;
    private Integer cornersVisitante;
    private Integer faltasLocal;
    private Integer faltasVisitante;

    public Estadistica() {}

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

    public Integer getPosesionLocal() {
        return posesionLocal != null ? posesionLocal : 0;
    }

    public void setPosesionLocal(Integer posesionLocal) {
        this.posesionLocal = posesionLocal;
    }

    public Integer getPosesionVisitante() {
        return posesionVisitante != null ? posesionVisitante : 0;
    }

    public void setPosesionVisitante(Integer posesionVisitante) {
        this.posesionVisitante = posesionVisitante;
    }

    public Integer getTirosLocal() {
        return tirosLocal != null ? tirosLocal : 0;
    }

    public void setTirosLocal(Integer tirosLocal) {
        this.tirosLocal = tirosLocal;
    }

    public Integer getTirosVisitante() {
        return tirosVisitante != null ? tirosVisitante : 0;
    }

    public void setTirosVisitante(Integer tirosVisitante) {
        this.tirosVisitante = tirosVisitante;
    }

    public Integer getTirosAPuertaLocal() {
        return tirosAPuertaLocal != null ? tirosAPuertaLocal : 0;
    }

    public void setTirosAPuertaLocal(Integer tirosAPuertaLocal) {
        this.tirosAPuertaLocal = tirosAPuertaLocal;
    }

    public Integer getTirosAPuertaVisitante() {
        return tirosAPuertaVisitante != null ? tirosAPuertaVisitante : 0;
    }

    public void setTirosAPuertaVisitante(Integer tirosAPuertaVisitante) {
        this.tirosAPuertaVisitante = tirosAPuertaVisitante;
    }

    public Integer getCornersLocal() {
        return cornersLocal != null ? cornersLocal : 0;
    }

    public void setCornersLocal(Integer cornersLocal) {
        this.cornersLocal = cornersLocal;
    }

    public Integer getCornersVisitante() {
        return cornersVisitante != null ? cornersVisitante : 0;
    }

    public void setCornersVisitante(Integer cornersVisitante) {
        this.cornersVisitante = cornersVisitante;
    }

    public Integer getFaltasLocal() {
        return faltasLocal != null ? faltasLocal : 0;
    }

    public void setFaltasLocal(Integer faltasLocal) {
        this.faltasLocal = faltasLocal;
    }

    public Integer getFaltasVisitante() {
        return faltasVisitante != null ? faltasVisitante : 0;
    }

    public void setFaltasVisitante(Integer faltasVisitante) {
        this.faltasVisitante = faltasVisitante;
    }
}