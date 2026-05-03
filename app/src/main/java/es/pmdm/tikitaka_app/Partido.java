package es.pmdm.tikitaka_app;

import java.util.Date;

public class Partido {
    private int idPartido;
    private Equipo equipoLocal;
    private Equipo equipoVisitante;
    private int golesLocal;
    private int golesVisitante;
    private Date fechaHora;
    private String estado; // "programado", "en_vivo", "finalizado"
    private String competicion;
    private int minutoActual;

    // Estadísticas
    private int posesionLocal;
    private int posesionVisitante;
    private int tirosAPuertaLocal;
    private int tirosAPuertaVisitante;
    private int tarjetasAmarillasLocal;
    private int tarjetasAmarillasVisitante;

    public Partido() {
    }

    public Partido(int idPartido, Equipo equipoLocal, Equipo equipoVisitante,
                   int golesLocal, int golesVisitante, Date fechaHora,
                   String estado, String competicion) {
        this.idPartido = idPartido;
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.competicion = competicion;
    }

    public int getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(int idPartido) {
        this.idPartido = idPartido;
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

    public int getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(int golesLocal) {
        this.golesLocal = golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(int golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCompeticion() {
        return competicion;
    }

    public void setCompeticion(String competicion) {
        this.competicion = competicion;
    }

    public int getMinutoActual() {
        return minutoActual;
    }

    public void setMinutoActual(int minutoActual) {
        this.minutoActual = minutoActual;
    }

    public int getPosesionLocal() {
        return posesionLocal;
    }

    public void setPosesionLocal(int posesionLocal) {
        this.posesionLocal = posesionLocal;
    }

    public int getPosesionVisitante() {
        return posesionVisitante;
    }

    public void setPosesionVisitante(int posesionVisitante) {
        this.posesionVisitante = posesionVisitante;
    }

    public int getTirosAPuertaLocal() {
        return tirosAPuertaLocal;
    }

    public void setTirosAPuertaLocal(int tirosAPuertaLocal) {
        this.tirosAPuertaLocal = tirosAPuertaLocal;
    }

    public int getTirosAPuertaVisitante() {
        return tirosAPuertaVisitante;
    }

    public void setTirosAPuertaVisitante(int tirosAPuertaVisitante) {
        this.tirosAPuertaVisitante = tirosAPuertaVisitante;
    }

    public int getTarjetasAmarillasLocal() {
        return tarjetasAmarillasLocal;
    }

    public void setTarjetasAmarillasLocal(int tarjetasAmarillasLocal) {
        this.tarjetasAmarillasLocal = tarjetasAmarillasLocal;
    }

    public int getTarjetasAmarillasVisitante() {
        return tarjetasAmarillasVisitante;
    }

    public void setTarjetasAmarillasVisitante(int tarjetasAmarillasVisitante) {
        this.tarjetasAmarillasVisitante = tarjetasAmarillasVisitante;
    }

    public boolean isEnVivo() {
        return "en_vivo".equals(estado);
    }

    public boolean isFinalizado() {
        return "finalizado".equals(estado);
    }
}
