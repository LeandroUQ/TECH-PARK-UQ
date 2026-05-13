package dao;

import datastructures.ListaEnlazada;
import model.Administrador;
import model.Operador;
import model.Personal;

public class PersonalDAO {

    private static PersonalDAO instancia;
    private final ListaEnlazada<Personal> personal = new ListaEnlazada<>();

    private PersonalDAO() {}

    public static PersonalDAO getInstance() {
        if (instancia == null) instancia = new PersonalDAO();
        return instancia;
    }

    public void agregar(Personal p) {
        personal.agregar(p);
    }

    public Personal buscarPorId(String id) {
        for (Personal p : personal) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    public ListaEnlazada<Personal> listarTodos() {
        return personal;
    }

    public ListaEnlazada<Operador> listarOperadores() {
        ListaEnlazada<Operador> resultado = new ListaEnlazada<>();
        for (Personal p : personal) {
            if (p instanceof Operador) resultado.agregar((Operador) p);
        }
        return resultado;
    }

    public ListaEnlazada<Administrador> listarAdministradores() {
        ListaEnlazada<Administrador> resultado = new ListaEnlazada<>();
        for (Personal p : personal) {
            if (p instanceof Administrador) resultado.agregar((Administrador) p);
        }
        return resultado;
    }

    public boolean eliminar(String id) {
        Personal p = buscarPorId(id);
        if (p == null) return false;
        return personal.eliminar(p);
    }
}
