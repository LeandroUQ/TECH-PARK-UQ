package dao;

import datastructures.ListaEnlazada;
import model.Atraccion;
import model.EstadoAtraccion;
import model.TipoAtraccion;

public class AtraccionDAO {

    private static AtraccionDAO instancia;
    private final ListaEnlazada<Atraccion> atracciones = new ListaEnlazada<>();

    private AtraccionDAO() {}

    public static AtraccionDAO getInstance() {
        if (instancia == null) instancia = new AtraccionDAO();
        return instancia;
    }

    public void agregar(Atraccion atraccion) {
        atracciones.agregar(atraccion);
    }

    public Atraccion buscarPorId(String id) {
        for (Atraccion a : atracciones) {
            if (a.getId().equals(id)) return a;
        }
        return null;
    }

    public ListaEnlazada<Atraccion> listarTodas() {
        return atracciones;
    }

    public ListaEnlazada<Atraccion> listarPorEstado(EstadoAtraccion estado) {
        ListaEnlazada<Atraccion> resultado = new ListaEnlazada<>();
        for (Atraccion a : atracciones) {
            if (a.getEstado() == estado) resultado.agregar(a);
        }
        return resultado;
    }

    public ListaEnlazada<Atraccion> listarPorTipo(TipoAtraccion tipo) {
        ListaEnlazada<Atraccion> resultado = new ListaEnlazada<>();
        for (Atraccion a : atracciones) {
            if (a.getTipo() == tipo) resultado.agregar(a);
        }
        return resultado;
    }

    public boolean eliminar(String id) {
        Atraccion a = buscarPorId(id);
        if (a == null) return false;
        return atracciones.eliminar(a);
    }
}
