package model;

public abstract class Personal {

    protected String id;
    protected String nombre;
    protected String documento;

    public Personal(String id, String nombre, String documento) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDocumento() { return documento; }
}
