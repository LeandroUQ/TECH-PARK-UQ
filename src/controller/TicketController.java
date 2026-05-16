package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Ticket;
import model.TipoTicket;
import model.Visitante;
import service.TicketService;
import service.VisitanteService;
import util.CorsUtil;
import util.HttpUtil;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class TicketController implements HttpHandler {

    private static final String PREFIX = "/api/tickets";
    private final TicketService service = TicketService.getInstance();
    private final VisitanteService visitanteService = VisitanteService.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (CorsUtil.handlePreflight(exchange)) return;
        CorsUtil.addHeaders(exchange);
        String method = exchange.getRequestMethod().toUpperCase();
        String extra = exchange.getRequestURI().getPath().substring(PREFIX.length());

        try {
            if (extra.isEmpty() || extra.equals("/")) {
                if ("GET".equals(method)) listarTodos(exchange);
                else if ("POST".equals(method)) emitir(exchange);
                else HttpUtil.sendError(exchange, 405, "Método no permitido");
            } else if (extra.startsWith("/visitante/")) {
                String documento = extra.substring("/visitante/".length());
                if ("GET".equals(method)) buscarPorDocumento(exchange, documento);
                else HttpUtil.sendError(exchange, 405, "Método no permitido");
            } else {
                String[] parts = extra.substring(1).split("/", 2);
                String id = parts[0];
                String action = parts.length > 1 ? parts[1] : "";
                if (action.isEmpty()) {
                    if ("GET".equals(method)) buscarPorId(exchange, id);
                    else HttpUtil.sendError(exchange, 405, "Método no permitido");
                } else if ("anular".equals(action) && "PUT".equals(method)) {
                    service.anular(id);
                    HttpUtil.sendJson(exchange, 200, "{\"ok\":true}");
                } else if ("validar".equals(action) && "GET".equals(method)) {
                    HttpUtil.sendJson(exchange, 200, "{\"valido\":" + service.validar(id) + "}");
                } else {
                    HttpUtil.sendError(exchange, 404, "Ruta no encontrada");
                }
            }
        } catch (IllegalArgumentException e) {
            HttpUtil.sendError(exchange, 400, e.getMessage());
        } catch (IllegalStateException e) {
            HttpUtil.sendError(exchange, 409, e.getMessage());
        } catch (Exception e) {
            HttpUtil.sendError(exchange, 500, "Error interno: " + e.getMessage());
        }
    }

    private void listarTodos(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Ticket t : service.listarTodos()) {
            if (!first) sb.append(",");
            sb.append(toJson(t));
            first = false;
        }
        HttpUtil.sendJson(exchange, 200, sb.append("]").toString());
    }

    private void emitir(HttpExchange exchange) throws IOException {
        Map<String, String> body = HttpUtil.parseBody(exchange);
        Visitante v = visitanteService.buscarPorDocumento(body.get("documento"));
        if (v == null) { HttpUtil.sendError(exchange, 404, "Visitante no encontrado"); return; }
        TipoTicket tipo = TipoTicket.valueOf(body.getOrDefault("tipo", "GENERAL"));
        double precio = Double.parseDouble(body.getOrDefault("precio", "0"));
        Ticket t = service.emitir(v, tipo, precio);
        HttpUtil.sendJson(exchange, 201, toJson(t));
    }

    private void buscarPorDocumento(HttpExchange exchange, String documento) throws IOException {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Ticket t : service.buscarPorDocumentoVisitante(documento)) {
            if (!first) sb.append(",");
            sb.append(toJson(t));
            first = false;
        }
        HttpUtil.sendJson(exchange, 200, sb.append("]").toString());
    }

    private void buscarPorId(HttpExchange exchange, String id) throws IOException {
        Ticket t = service.buscarPorId(id);
        if (t == null) { HttpUtil.sendError(exchange, 404, "Ticket no encontrado"); return; }
        HttpUtil.sendJson(exchange, 200, toJson(t));
    }

    private static String toJson(Ticket t) {
        return String.format(Locale.ROOT,
            "{\"id\":\"%s\",\"tipo\":\"%s\",\"precio\":%.2f,\"activo\":%b,\"titular\":%s}",
            HttpUtil.escape(t.getId()), t.getTipo(), t.getPrecio(), t.isActivo(),
            VisitanteController.toJson(t.getTitular())
        );
    }
}
