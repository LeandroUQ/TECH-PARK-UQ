package dao;

import datastructures.ListaEnlazada;
import model.Ticket;

public class TicketDAO {

    private static TicketDAO instancia;
    private final ListaEnlazada<Ticket> tickets = new ListaEnlazada<>();

    private TicketDAO() {}

    public static TicketDAO getInstance() {
        if (instancia == null) instancia = new TicketDAO();
        return instancia;
    }

    public void agregar(Ticket ticket) {
        tickets.agregar(ticket);
    }

    public Ticket buscarPorId(String id) {
        for (Ticket t : tickets) {
            if (t.getId().equals(id)) return t;
        }
        return null;
    }

    public ListaEnlazada<Ticket> buscarPorDocumentoVisitante(String documento) {
        ListaEnlazada<Ticket> resultado = new ListaEnlazada<>();
        for (Ticket t : tickets) {
            if (t.getTitular().getDocumento().equals(documento)) resultado.agregar(t);
        }
        return resultado;
    }

    public ListaEnlazada<Ticket> listarTodos() {
        return tickets;
    }

    public boolean eliminar(String id) {
        Ticket t = buscarPorId(id);
        if (t == null) return false;
        return tickets.eliminar(t);
    }
}
