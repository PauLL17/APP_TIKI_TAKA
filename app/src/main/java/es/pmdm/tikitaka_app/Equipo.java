package es.pmdm.tikitaka_app;

import java.io.Serializable;

public class Equipo implements Serializable {
    private Long id;
    private String nombre;
    private String ciudad;
    private String estadio;
    private String escudoUrl;
    private String entrenador;
    private Integer anioFundacion;

    public Equipo() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstadio() {
        return estadio;
    }

    public void setEstadio(String estadio) {
        this.estadio = estadio;
    }

    public String getEscudoUrl() {
        return escudoUrl;
    }

    public void setEscudoUrl(String escudoUrl) {
        this.escudoUrl = escudoUrl;
    }

    public String getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(String entrenador){
        this.entrenador = entrenador;
    }

    public Integer getAnioFundacion() {
        return anioFundacion;
    }

    public void setAnioFundacion(Integer anioFundacion) {
        this.anioFundacion = anioFundacion;
    }
}