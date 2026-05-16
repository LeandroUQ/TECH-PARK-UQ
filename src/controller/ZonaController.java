package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Zona;
import service.ZonaService;
import util.CorsUtil;
import util.HttpUtil;

import java.io.IOException;
import java.util.Map;

public class ZonaController implements HttpHandler {

    private static final String PREFIX = "/api/zonas";
    private final ZonaService service = ZonaService.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (CorsUtil.handlePreflight(exchange)) return;
        CorsUtil.addHeaders(exchange);
        String method = exchange.getRequestMethod().toUpperCase();
        String extra = exchange.getRequestURI().getPath().substring(PREFIX.length());

        try {
            if (extra.isEmpty() || extra.equals("/")) {
                if ("GET".equals(method)) listarTodas(exchange);
                else if ("POST".equals(method)) registrar(exchange);
                else HttpUtil.sendError(exchange, 405, "Método no permitido");
            } else {
                String[] parts = extra.substring(1).split("/", 2);
                String id = parts[0];
                String action = parts.length > 1 ? parts[1] : "";
                if (action.isEmpty()) {
                    if ("GET".equals(method)) buscarPorId(exchange, id);
                    else if ("DELETE".equals(method)) eliminar(exchange, id);
                    else HttpUtil.sendError(exchange, 405, "Método no permitido");
                } else if ("ingresar".equals(action) && "POST".equals(method)) {
                    service.ingresarVisitante(id);
                    HttpUtil.sendJson(exchange, 200, "{\"ok\":true}");
                } else if ("retirar".equals(action) && "POST".equals(method)) {
                    service.retirarVisitante(id);
                    HttpUtil.sendJson(exchange, 200, "{\"ok\":true}");
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

    private void listarTodas(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Zona z : service.listarTodas()) {
            if (!first) sb.append(",");
            sb.append(toJson(z));
            first = false;
        }
        HttpUtil.sendJson(exchange, 200, sb.append("]").toString());
    }

    private void registrar(HttpExchange exchange) throws IOException {
        Map<String, String> body = HttpUtil.parseBody(exchange);
        Zona z = new Zona(
            body.get("id"),
            body.get("nombre"),
            Integer.parseInt(body.getOrDefault("capacidadMaxima", "0"))
        );
        service.registrar(z);
        HttpUtil.sendJson(exchange, 201, toJson(z));
    }

    private void buscarPorId(HttpExchange exchange, String id) throws IOException {
        Zona z = service.buscarPorId(id);
        if (z == null) { HttpUtil.sendError(exchange, 404, "Zona no encontrada"); return; }
        HttpUtil.sendJson(exchange, 200, toJson(z));
    }

    private void eliminar(HttpExchange exchange, String id) throws IOException {
        if (!service.eliminar(id)) {
            HttpUtil.sendError(exchange, 404, "Zona no encontrada");
        } else {
            HttpUtil.sendJson(exchange, 200, "{\"eliminado\":true}");
        }
    }

    static String toJson(Zona z) {
        if (z == null) return "null";
        return String.format(
            "{\"id\":\"%s\",\"nombre\":\"%s\",\"capacidadMaxima\":%d,\"visitantesActuales\":%d,\"llena\":%b}",
            HttpUtil.escape(z.getId()), HttpUtil.escape(z.getNombre()),
            z.getCapacidadMaxima(), z.getVisitantesActuales(), z.estaLlena()
        );
    }
}
