package service;

import datastructures.ColaPrioridad;
import model.EntradaCola;
import model.TipoTicket;
import model.Visitante;

import java.util.HashMap;
import java.util.Map;

public class ColaService {

    private static ColaService instancia;
    // Una cola de prioridad por atracción (clave = id de atracción)
    private final Map<String, ColaPrioridad<EntradaCola>> colas = new HashMap<>();

    private ColaService() {}

    public static ColaService getInstance() {
        if (instancia == null) instancia = new ColaService();
        return instancia;
    }

    public void encolar(String atraccionId, Visitante visitante) {
        int prioridad = visitante.getTipoTicket() == TipoTicket.FAST_PASS ? 1 : 2;
        obtenerCola(atraccionId).encolar(new EntradaCola(visitante, prioridad));
    }

    public EntradaCola atender(String atraccionId) {
        ColaPrioridad<EntradaCola> cola = obtenerCola(atraccionId);
        if (cola.estaVacia()) throw new IllegalStateException("No hay visitantes en cola para: " + atraccionId);
        return cola.desencolar();
    }

    public EntradaCola verSiguiente(String atraccionId) {
        ColaPrioridad<EntradaCola> cola = obtenerCola(atraccionId);
        if (cola.estaVacia()) return null;
        return cola.peek();
    }

    public int tamano(String atraccionId) {
        return obtenerCola(atraccionId).tamano();
    }

    public boolean estaVacia(String atraccionId) {
        return obtenerCola(atraccionId).estaVacia();
    }

    public void limpiarCola(String atraccionId) {
        colas.put(atraccionId, new ColaPrioridad<>());
    }

    private ColaPrioridad<EntradaCola> obtenerCola(String atraccionId) {
        return colas.computeIfAbsent(atraccionId, k -> new ColaPrioridad<>());
    }
}