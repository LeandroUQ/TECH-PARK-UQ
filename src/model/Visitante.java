package model;

public class Visitante {

    private String nombre;
    private String documento;
    private int edad;
    private double estatura;
    private double saldoVirtual;
    private String fotoPase;
    private TipoTicket tipoTicket;

    public Visitante(String nombre, String documento, int edad, double estatura,
                     double saldoVirtual, TipoTicket tipoTicket) {
        this.nombre = nombre;
        this.documento = documento;
        this.edad = edad;
        this.estatura = estatura;
        this.saldoVirtual = saldoVirtual;
        this.tipoTicket = tipoTicket;
    }

    public boolean tieneSaldoSuficiente(double costo) {
        return saldoVirtual >= costo;
    }

    public void descontarSaldo(double costo) {
        saldoVirtual -= costo;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDocumento() { return documento; }
    public int getEdad() { return edad; }
    public double getEstatura() { return estatura; }
    public double getSaldoVirtual() { return saldoVirtual; }
    public void setSaldoVirtual(double saldoVirtual) { this.saldoVirtual = saldoVirtual; }
    public String getFotoPase() { return fotoPase; }
    public void setFotoPase(String fotoPase) { this.fotoPase = fotoPase; }
    public TipoTicket getTipoTicket() { return tipoTicket; }
    public void setTipoTicket(TipoTicket tipoTicket) { this.tipoTicket = tipoTicket; }
}
