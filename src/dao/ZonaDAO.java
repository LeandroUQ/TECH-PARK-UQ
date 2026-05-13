package dao;

import datastructures.ListaEnlazada;
import model.Zona;

public class ZonaDAO {

    private static ZonaDAO instancia;
    private final ListaEnlazada<Zona> zonas = new ListaEnlazada<>();

    private ZonaDAO() {}

    public static ZonaDAO getInstance() {
        if (instancia == null) instancia = new ZonaDAO();
        return instancia;
    }

    public void agregar(Zona zona) {
        zonas.agregar(zona);
    }

    public Zona buscarPorId(String id) {
        for (Zona z : zonas) {
            if (z.getId().equals(id)) return z;
        }
        return null;
    }

    public ListaEnlazada<Zona> listarTodas() {
        return zonas;
    }

    public boolean eliminar(String id) {
        Zona z = buscarPorId(id);
        if (z == null) return false;
        return zonas.eliminar(z);
    }
}
