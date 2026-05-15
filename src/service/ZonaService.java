package service;

import dao.ZonaDAO;
import datastructures.ListaEnlazada;
import model.Zona;

public class ZonaService {

    private static ZonaService instancia;
    private final ZonaDAO dao = ZonaDAO.getInstance();

    private ZonaService() {}

    public static ZonaService getInstance() {
        if (instancia == null) instancia = new ZonaService();
        return instancia;
    }

    public void registrar(Zona zona) {
        if (dao.buscarPorId(zona.getId()) != null)
            throw new IllegalArgumentException("Ya existe una zona con id: " + zona.getId());
        dao.agregar(zona);
    }

    public Zona buscarPorId(String id) {
        return dao.buscarPorId(id);
    }

    public ListaEnlazada<Zona> listarTodas() {
        return dao.listarTodas();
    }

    public boolean eliminar(String id) {
        return dao.eliminar(id);
    }

    public void ingresarVisitante(String zonaId) {
        Zona zona = obtenerOFallar(zonaId);
        if (zona.estaLlena())
            throw new IllegalStateException("La zona " + zonaId + " está al máximo de capacidad");
        zona.setVisitantesActuales(zona.getVisitantesActuales() + 1);
    }

    public void retirarVisitante(String zonaId) {
        Zona zona = obtenerOFallar(zonaId);
        if (zona.getVisitantesActuales() > 0)
            zona.setVisitantesActuales(zona.getVisitantesActuales() - 1);
    }

    private Zona obtenerOFallar(String id) {
        Zona z = dao.buscarPorId(id);
        if (z == null) throw new IllegalArgumentException("Zona no encontrada: " + id);
        return z;
    }
}
