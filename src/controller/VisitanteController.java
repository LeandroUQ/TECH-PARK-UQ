package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.TipoTicket;
import model.Visitante;
import service.VisitanteService;
import util.CorsUtil;
import util.HttpUtil;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class VisitanteController implements HttpHandler {

    private static final String PREFIX = "/api/visitantes";
    private final VisitanteService service = VisitanteService.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (CorsUtil.handlePreflight(exchange)) return;
        CorsUtil.addHeaders(exchange);
        String method = exchange.getRequestMethod().toUpperCase();
        String extra = exchange.getRequestURI().getPath().substring(PREFIX.length());

        try {
            if (extra.isEmpty() || extra.equals("/")) {
                if ("GET".equals(method)) listarTodos(exchange);
                else if ("POST".equals(method)) registrar(exchange);
                else HttpUtil.sendError(exchange, 405, "Método no permitido");
            } else if ("/aforo".equals(extra)) {
                if ("GET".equals(method)) aforo(exchange);
                else HttpUtil.sendError(exchange, 405, "Método no permitido");
            } else {
                String documento = extra.substring(1);
                if ("GET".equals(method)) buscarPorDocumento(exchange, documento);
                else if ("DELETE".equals(method)) eliminar(exchange, documento);
                else HttpUtil.sendError(exchange, 405, "Método no permitido");
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
        for (Visitante v : service.listarTodos()) {
            if (!first) sb.append(",");
            sb.append(toJson(v));
            first = false;
        }
        HttpUtil.sendJson(exchange, 200, sb.append("]").toString());
    }

    private void registrar(HttpExchange exchange) throws IOException {
        Map<String, String> body = HttpUtil.parseBody(exchange);
        Visitante v = new Visitante(
            body.get("nombre"),
            body.get("documento"),
            Integer.parseInt(body.getOrDefault("edad", "0")),
            Double.parseDouble(body.getOrDefault("estatura", "0")),
            Double.parseDouble(body.getOrDefault("saldoVirtual", "0")),
            TipoTicket.valueOf(body.getOrDefault("tipoTicket", "GENERAL"))
        );
        service.registrar(v);
        HttpUtil.sendJson(exchange, 201, toJson(v));
    }

    private void aforo(HttpExchange exchange) throws IOException {
        HttpUtil.sendJson(exchange, 200, String.format(Locale.ROOT,
            "{\"actual\":%d,\"maximo\":%d,\"lleno\":%b}",
            service.contarVisitantes(), VisitanteService.LIMITE_VISITANTES, service.aforoLleno()
        ));
    }

    private void buscarPorDocumento(HttpExchange exchange, String documento) throws IOException {
        Visitante v = service.buscarPorDocumento(documento);
        if (v == null) { HttpUtil.sendError(exchange, 404, "Visitante no encontrado"); return; }
        HttpUtil.sendJson(exchange, 200, toJson(v));
    }

    private void eliminar(HttpExchange exchange, String documento) throws IOException {
        if (!service.eliminar(documento)) {
            HttpUtil.sendError(exchange, 404, "Visitante no encontrado");
        } else {
            HttpUtil.sendJson(exchange, 200, "{\"eliminado\":true}");
        }
    }

    static String toJson(Visitante v) {
        return String.format(Locale.ROOT,
            "{\"nombre\":\"%s\",\"documento\":\"%s\",\"edad\":%d," +
            "\"estatura\":%.2f,\"saldoVirtual\":%.2f,\"tipoTicket\":\"%s\",\"fotoPase\":%s}",
            HttpUtil.escape(v.getNombre()), HttpUtil.escape(v.getDocumento()),
            v.getEdad(), v.getEstatura(), v.getSaldoVirtual(), v.getTipoTicket(),
            v.getFotoPase() != null ? "\"" + HttpUtil.escape(v.getFotoPase()) + "\"" : "null"
        );
    }
}
