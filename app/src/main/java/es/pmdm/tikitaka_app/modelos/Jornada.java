package es.pmdm.tikitaka_app.modelos;

import java.io.Serializable;

public class Jornada implements Serializable {
    private Long id;
    private Integer numero;
    private Long competicionId;

    public Jornada() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Long getCompeticionId() {
        return competicionId;
    }

    public void setCompeticionId(Long competicionId) {
        this.competicionId = competicionId;
    }
}
