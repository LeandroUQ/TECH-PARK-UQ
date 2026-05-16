package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.EntradaCola;
import model.Visitante;
import service.ColaService;
import service.VisitanteService;
import util.CorsUtil;
import util.HttpUtil;

import java.io.IOException;
import java.util.Map;

public class ColaController implements HttpHandler {

    private static final String PREFIX = "/api/cola";
    private final ColaService service = ColaService.getInstance();
    private final VisitanteService visitanteService = VisitanteService.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (CorsUtil.handlePreflight(exchange)) return;
        CorsUtil.addHeaders(exchange);
        String method = exchange.getRequestMethod().toUpperCase();
        String extra = exchange.getRequestURI().getPath().substring(PREFIX.length());

        try {
            if (extra.isEmpty() || extra.equals("/")) {
                HttpUtil.sendError(exchange, 400, "Especifica un id de atracción");
                return;
            }
            String[] parts = extra.substring(1).split("/", 2);
            String atraccionId = parts[0];
            String action = parts.length > 1 ? parts[1] : "";

            switch (action) {
                case "encolar":
                    if (!"POST".equals(method)) { HttpUtil.sendError(exchange, 405, "Método no permitido"); return; }
                    encolar(exchange, atraccionId);
                    break;
                case "atender":
                    if (!"POST".equals(method)) { HttpUtil.sendError(exchange, 405, "Método no permitido"); return; }
                    HttpUtil.sendJson(exchange, 200, entradaToJson(service.atender(atraccionId)));
                    break;
                case "siguiente":
                    if (!"GET".equals(method)) { HttpUtil.sendError(exchange, 405, "Método no permitido"); return; }
                    HttpUtil.sendJson(exchange, 200, entradaToJson(service.verSiguiente(atraccionId)));
                    break;
                case "tamano":
                    if (!"GET".equals(method)) { HttpUtil.sendError(exchange, 405, "Método no permitido"); return; }
                    HttpUtil.sendJson(exchange, 200, "{\"tamano\":" + service.tamano(atraccionId) + "}");
                    break;
                case "":
                    if ("DELETE".equals(method)) {
                        service.limpiarCola(atraccionId);
                        HttpUtil.sendJson(exchange, 200, "{\"ok\":true}");
                    } else {
                        HttpUtil.sendError(exchange, 405, "Método no permitido");
                    }
                    break;
                default:
                    HttpUtil.sendError(exchange, 404, "Ruta no encontrada");
            }
        } catch (IllegalArgumentException e) {
            HttpUtil.sendError(exchange, 400, e.getMessage());
        } catch (IllegalStateException e) {
            HttpUtil.sendError(exchange, 409, e.getMessage());
        } catch (Exception e) {
            HttpUtil.sendError(exchange, 500, "Error interno: " + e.getMessage());
        }
    }

    private void encolar(HttpExchange exchange, String atraccionId) throws IOException {
        Map<String, String> body = HttpUtil.parseBody(exchange);
        Visitante v = visitanteService.buscarPorDocumento(body.get("documento"));
        if (v == null) { HttpUtil.sendError(exchange, 404, "Visitante no encontrado"); return; }
        service.encolar(atraccionId, v);
        HttpUtil.sendJson(exchange, 200, "{\"ok\":true,\"tamano\":" + service.tamano(atraccionId) + "}");
    }

    private static String entradaToJson(EntradaCola e) {
        if (e == null) return "null";
        return String.format("{\"visitante\":%s,\"prioridad\":%d}",
            VisitanteController.toJson(e.getVisitante()), e.getPrioridad());
    }
}
