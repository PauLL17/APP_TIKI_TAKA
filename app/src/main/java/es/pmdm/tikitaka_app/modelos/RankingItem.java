package es.pmdm.tikitaka_app.modelos;

public class RankingItem
{
    private String nombre;
    private int total;

    public RankingItem(String nombre, int total) {
        this.nombre = nombre;
        this.total  = total;
    }

    public String getNombre() {
        return nombre;
    }

    public int getTotal() {
        return total;
    }
}
