package es.pmdm.tikitaka_app;

import java.util.Date;

public class Noticia {
    private int idNoticia;
    private String titulo;
    private String contenido;
    private Equipo equipoRelacionado;
    private Date fechaPublicacion;

    public Noticia() {
    }

    public Noticia(int idNoticia, String titulo, String contenido, Date fechaPublicacion) {
        this.idNoticia = idNoticia;
        this.titulo = titulo;
        this.contenido = contenido;
        this.fechaPublicacion = fechaPublicacion;
    }

    public int getIdNoticia() {
        return idNoticia;
    }

    public void setIdNoticia(int idNoticia) {
        this.idNoticia = idNoticia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Equipo getEquipoRelacionado() {
        return equipoRelacionado;
    }

    public void setEquipoRelacionado(Equipo equipoRelacionado) {
        this.equipoRelacionado = equipoRelacionado;
    }

    public Date getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Date fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }
}
