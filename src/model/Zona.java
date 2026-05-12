package model;

public class Zona {

    private String id;
    private String nombre;
    private int capacidadMaxima;
    private int visitantesActuales;

    public Zona(String id, String nombre, int capacidadMaxima) {
        this.id = id;
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
        this.visitantesActuales = 0;
    }

    public boolean estaLlena() {
        return visitantesActuales >= capacidadMaxima;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public int getCapacidadMaxima() { return capacidadMaxima; }
    public int getVisitantesActuales() { return visitantesActuales; }
    public void setVisitantesActuales(int visitantesActuales) { this.visitantesActuales = visitantesActuales; }
}
