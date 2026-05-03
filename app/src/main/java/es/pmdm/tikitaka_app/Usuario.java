package es.pmdm.tikitaka_app;
public class Usuario {
    private int idUsuario;
    private String email;
    private String nombre;
    private String tipoUsuario;
    private Equipo equipoFavorito;

    public Usuario() {
    }

    public Usuario(int idUsuario, String email, String nombre, String tipoUsuario) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.nombre = nombre;
        this.tipoUsuario = tipoUsuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Equipo getEquipoFavorito() {
        return equipoFavorito;
    }

    public void setEquipoFavorito(Equipo equipoFavorito) {
        this.equipoFavorito = equipoFavorito;
    }
}
