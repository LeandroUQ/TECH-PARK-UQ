package service;

import dao.TicketDAO;
import datastructures.ListaEnlazada;
import model.Ticket;
import model.TipoTicket;
import model.Visitante;

import java.util.UUID;

public class TicketService {

    private static TicketService instancia;
    private final TicketDAO dao = TicketDAO.getInstance();

    private TicketService() {}

    public static TicketService getInstance() {
        if (instancia == null) instancia = new TicketService();
        return instancia;
    }

    public Ticket emitir(Visitante visitante, TipoTicket tipo, double precio) {
        if (!visitante.tieneSaldoSuficiente(precio))
            throw new IllegalStateException("Saldo insuficiente para emitir el ticket");
        visitante.descontarSaldo(precio);
        String id = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Ticket ticket = new Ticket(id, tipo, visitante, precio);
        dao.agregar(ticket);
        return ticket;
    }

    public Ticket buscarPorId(String id) {
        return dao.buscarPorId(id);
    }

    public ListaEnlazada<Ticket> buscarPorDocumentoVisitante(String documento) {
        return dao.buscarPorDocumentoVisitante(documento);
    }

    public ListaEnlazada<Ticket> listarTodos() {
        return dao.listarTodos();
    }

    public void anular(String id) {
        Ticket t = dao.buscarPorId(id);
        if (t == null) throw new IllegalArgumentException("Ticket no encontrado: " + id);
        if (!t.isActivo()) throw new IllegalStateException("El ticket ya está anulado: " + id);
        t.setActivo(false);
    }

    public boolean validar(String id) {
        Ticket t = dao.buscarPorId(id);
        return t != null && t.isActivo();
    }
}
