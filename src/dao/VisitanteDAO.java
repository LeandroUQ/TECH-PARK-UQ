package dao;

import datastructures.ListaEnlazada;
import model.Visitante;

public class VisitanteDAO {

    private static VisitanteDAO instancia;
    private final ListaEnlazada<Visitante> visitantes = new ListaEnlazada<>();

    private VisitanteDAO() {}

    public static VisitanteDAO getInstance() {
        if (instancia == null) instancia = new VisitanteDAO();
        return instancia;
    }

    public void agregar(Visitante visitante) {
        visitantes.agregar(visitante);
    }

    public Visitante buscarPorDocumento(String documento) {
        for (Visitante v : visitantes) {
            if (v.getDocumento().equals(documento)) return v;
        }
        return null;
    }

    public ListaEnlazada<Visitante> listarTodos() {
        return visitantes;
    }

    public boolean eliminar(String documento) {
        Visitante v = buscarPorDocumento(documento);
        if (v == null) return false;
        return visitantes.eliminar(v);
    }
}
