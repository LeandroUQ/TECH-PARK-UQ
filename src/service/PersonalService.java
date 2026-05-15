package service;

import dao.PersonalDAO;
import datastructures.ListaEnlazada;
import model.Administrador;
import model.Operador;
import model.Personal;

public class PersonalService {

    private static PersonalService instancia;
    private final PersonalDAO dao = PersonalDAO.getInstance();

    private PersonalService() {}

    public static PersonalService getInstance() {
        if (instancia == null) instancia = new PersonalService();
        return instancia;
    }

    public void registrar(Personal p) {
        if (dao.buscarPorId(p.getId()) != null)
            throw new IllegalArgumentException("Ya existe un empleado con id: " + p.getId());
        dao.agregar(p);
    }

    public Personal buscarPorId(String id) {
        return dao.buscarPorId(id);
    }

    public ListaEnlazada<Personal> listarTodos() {
        return dao.listarTodos();
    }

    public ListaEnlazada<Operador> listarOperadores() {
        return dao.listarOperadores();
    }

    public ListaEnlazada<Administrador> listarAdministradores() {
        return dao.listarAdministradores();
    }

    public boolean eliminar(String id) {
        return dao.eliminar(id);
    }
}
