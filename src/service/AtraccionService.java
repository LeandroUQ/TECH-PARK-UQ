package service;

import dao.AtraccionDAO;
import datastructures.ListaEnlazada;
import model.Atraccion;
import model.EstadoAtraccion;
import model.TipoAtraccion;

public class AtraccionService {

    private static AtraccionService instancia;
    private final AtraccionDAO dao = AtraccionDAO.getInstance();

    private AtraccionService() {}

    public static AtraccionService getInstance() {
        if (instancia == null) instancia = new AtraccionService();
        return instancia;
    }

    public void registrar(Atraccion atraccion) {
        if (dao.buscarPorId(atraccion.getId()) != null)
            throw new IllegalArgumentException("Ya existe una atracción con id: " + atraccion.getId());
        dao.agregar(atraccion);
    }

    public Atraccion buscarPorId(String id) {
        return dao.buscarPorId(id);
    }

    public ListaEnlazada<Atraccion> listarTodas() {
        return dao.listarTodas();
    }

    public ListaEnlazada<Atraccion> listarActivas() {
        return dao.listarPorEstado(EstadoAtraccion.ACTIVA);
    }

    public ListaEnlazada<Atraccion> listarPorTipo(TipoAtraccion tipo) {
        return dao.listarPorTipo(tipo);
    }

    public boolean eliminar(String id) {
        return dao.eliminar(id);
    }

    // Registra un uso del ciclo; pasa a mantenimiento automáticamente al llegar al límite.
    public void registrarUso(String id) {
        Atraccion a = obtenerOFallar(id);
        if (a.getEstado() != EstadoAtraccion.ACTIVA)
            throw new IllegalStateException("La atracción " + id + " no está activa");
        a.setContadorAcumulado(a.getContadorAcumulado() + 1);
        if (a.requiereMantenimiento()) {
            a.setEstado(EstadoAtraccion.EN_MANTENIMIENTO);
            a.setMotivoCierre("Mantenimiento preventivo (límite de usos alcanzado)");
        }
    }

    public void cambiarEstado(String id, EstadoAtraccion nuevoEstado, String motivo) {
        Atraccion a = obtenerOFallar(id);
        a.setEstado(nuevoEstado);
        a.setMotivoCierre(nuevoEstado == EstadoAtraccion.ACTIVA ? null : motivo);
        if (nuevoEstado == EstadoAtraccion.ACTIVA) a.setContadorAcumulado(0);
    }

    // Cierra todas las atracciones vulnerables al clima (ACUATICA y MECANICA_ALTURA).
    public void activarAlertaClimatica() {
        for (Atraccion a : dao.listarTodas()) {
            if (a.esVulnerableAlClima() && a.getEstado() == EstadoAtraccion.ACTIVA) {
                a.setEstado(EstadoAtraccion.CERRADA);
                a.setMotivoCierre("ALERTA_CLIMATICA");
            }
        }
    }

    // Reactiva únicamente las atracciones que fueron cerradas por alerta climática.
    public void desactivarAlertaClimatica() {
        for (Atraccion a : dao.listarTodas()) {
            if ("ALERTA_CLIMATICA".equals(a.getMotivoCierre())) {
                a.setEstado(EstadoAtraccion.ACTIVA);
                a.setMotivoCierre(null);
            }
        }
    }

    private Atraccion obtenerOFallar(String id) {
        Atraccion a = dao.buscarPorId(id);
        if (a == null) throw new IllegalArgumentException("Atracción no encontrada: " + id);
        return a;
    }
}