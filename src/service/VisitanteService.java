package service;

import dao.VisitanteDAO;
import datastructures.ListaEnlazada;
import model.Visitante;

public class VisitanteService {

    public static final int LIMITE_VISITANTES = 500;

    private static VisitanteService instancia;
    private final VisitanteDAO dao = VisitanteDAO.getInstance();

    private VisitanteService() {}

    public static VisitanteService getInstance() {
        if (instancia == null) instancia = new VisitanteService();
        return instancia;
    }

    public void registrar(Visitante visitante) {
        if (dao.listarTodos().tamano() >= LIMITE_VISITANTES)
            throw new IllegalStateException("Aforo máximo alcanzado (" + LIMITE_VISITANTES + " visitantes)");
        if (dao.buscarPorDocumento(visitante.getDocumento()) != null)
            throw new IllegalArgumentException("Ya existe un visitante con documento: " + visitante.getDocumento());
        dao.agregar(visitante);
    }

    public Visitante buscarPorDocumento(String documento) {
        return dao.buscarPorDocumento(documento);
    }

    public ListaEnlazada<Visitante> listarTodos() {
        return dao.listarTodos();
    }

    public boolean eliminar(String documento) {
        return dao.eliminar(documento);
    }

    public int contarVisitantes() {
        return dao.listarTodos().tamano();
    }

    public boolean aforoLleno() {
        return dao.listarTodos().tamano() >= LIMITE_VISITANTES;
    }
}
