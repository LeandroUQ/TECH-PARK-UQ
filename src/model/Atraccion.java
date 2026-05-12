package model;

public class Atraccion {

    public static final int LIMITE_MANTENIMIENTO = 500;

    private String id;
    private String nombre;
    private TipoAtraccion tipo;
    private int capacidadMaximaPorCiclo;
    private double alturaMinima;
    private int edadMinima;
    private double costoAdicional;
    private int contadorAcumulado;
    private int tiempoEsperaEstimado;
    private EstadoAtraccion estado;
    private String motivoCierre;
    private Zona zona;

    public Atraccion(String id, String nombre, TipoAtraccion tipo, int capacidadMaximaPorCiclo,
                     double alturaMinima, int edadMinima, double costoAdicional, Zona zona) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidadMaximaPorCiclo = capacidadMaximaPorCiclo;
        this.alturaMinima = alturaMinima;
        this.edadMinima = edadMinima;
        this.costoAdicional = costoAdicional;
        this.zona = zona;
        this.contadorAcumulado = 0;
        this.tiempoEsperaEstimado = 0;
        this.estado = EstadoAtraccion.ACTIVA;
    }

    public boolean requiereMantenimiento() {
        return contadorAcumulado >= LIMITE_MANTENIMIENTO;
    }

    // true si debe cerrarse ante alerta climática
    public boolean esVulnerableAlClima() {
        return tipo == TipoAtraccion.ACUATICA || tipo == TipoAtraccion.MECANICA_ALTURA;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public TipoAtraccion getTipo() { return tipo; }
    public int getCapacidadMaximaPorCiclo() { return capacidadMaximaPorCiclo; }
    public double getAlturaMinima() { return alturaMinima; }
    public int getEdadMinima() { return edadMinima; }
    public double getCostoAdicional() { return costoAdicional; }
    public int getContadorAcumulado() { return contadorAcumulado; }
    public void setContadorAcumulado(int contadorAcumulado) { this.contadorAcumulado = contadorAcumulado; }
    public int getTiempoEsperaEstimado() { return tiempoEsperaEstimado; }
    public void setTiempoEsperaEstimado(int tiempoEsperaEstimado) { this.tiempoEsperaEstimado = tiempoEsperaEstimado; }
    public EstadoAtraccion getEstado() { return estado; }
    public void setEstado(EstadoAtraccion estado) { this.estado = estado; }
    public String getMotivoCierre() { return motivoCierre; }
    public void setMotivoCierre(String motivoCierre) { this.motivoCierre = motivoCierre; }
    public Zona getZona() { return zona; }
    public void setZona(Zona zona) { this.zona = zona; }
}
