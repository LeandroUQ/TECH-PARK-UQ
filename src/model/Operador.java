package model;

public class Operador extends Personal {

    private Zona zonaAsignada;

    public Operador(String id, String nombre, String documento, Zona zonaAsignada) {
        super(id, nombre, documento);
        this.zonaAsignada = zonaAsignada;
    }

    public Zona getZonaAsignada() { return zonaAsignada; }
    public void setZonaAsignada(Zona zonaAsignada) { this.zonaAsignada = zonaAsignada; }
}
