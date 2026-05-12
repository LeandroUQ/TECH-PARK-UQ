package model;

public class Ticket {

    private String id;
    private TipoTicket tipo;
    private Visitante titular;
    private double precio;
    private boolean activo;

    public Ticket(String id, TipoTicket tipo, Visitante titular, double precio) {
        this.id = id;
        this.tipo = tipo;
        this.titular = titular;
        this.precio = precio;
        this.activo = true;
    }

    public String getId() { return id; }
    public TipoTicket getTipo() { return tipo; }
    public Visitante getTitular() { return titular; }
    public double getPrecio() { return precio; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
