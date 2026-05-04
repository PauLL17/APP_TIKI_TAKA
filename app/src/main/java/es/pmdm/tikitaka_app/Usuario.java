package es.pmdm.tikitaka_app;

import java.io.Serializable;

public class Usuario implements Serializable {
    private Long id;
    private String nombreUsuario;
    private String email;
    private Long equipoFavoritoId;

    public Usuario() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getEquipoFavoritoId() {
        return equipoFavoritoId;
    }

    public void setEquipoFavoritoId(Long equipoFavoritoId) {
        this.equipoFavoritoId = equipoFavoritoId;
    }
}