package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Atraccion;
import model.EstadoAtraccion;
import model.TipoAtraccion;
import model.Zona;
import service.AtraccionService;
import service.ZonaService;
import util.CorsUtil;
import util.HttpUtil;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class AtraccionController implements HttpHandler {

    private static final String PREFIX = "/api/atracciones";
    private final AtraccionService service = AtraccionService.getInstance();
    private final ZonaService zonaService = ZonaService.getInstance();

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
            } else if ("/alerta-climatica/activar".equals(extra) && "POST".equals(method)) {
                service.activarAlertaClimatica();
                HttpUtil.sendJson(exchange, 200, "{\"ok\":true}");
            } else if ("/alerta-climatica/desactivar".equals(extra) && "POST".equals(method)) {
                service.desactivarAlertaClimatica();
                HttpUtil.sendJson(exchange, 200, "{\"ok\":true}");
            } else {
                String[] parts = extra.substring(1).split("/", 2);
                String id = parts[0];
                String action = parts.length > 1 ? parts[1] : "";
                if (action.isEmpty()) {
                    if ("GET".equals(method)) buscarPorId(exchange, id);
                    else if ("DELETE".equals(method)) eliminar(exchange, id);
                    else HttpUtil.sendError(exchange, 405, "Método no permitido");
                } else if ("uso".equals(action) && "POST".equals(method)) {
                    service.registrarUso(id);
                    HttpUtil.sendJson(exchange, 200, "{\"ok\":true}");
                } else if ("estado".equals(action) && "PUT".equals(method)) {
                    cambiarEstado(exchange, id);
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
        for (Atraccion a : service.listarTodas()) {
            if (!first) sb.append(",");
            sb.append(toJson(a));
            first = false;
        }
        HttpUtil.sendJson(exchange, 200, sb.append("]").toString());
    }

    private void registrar(HttpExchange exchange) throws IOException {
        Map<String, String> body = HttpUtil.parseBody(exchange);
        Zona zona = zonaService.buscarPorId(body.get("zonaId"));
        if (zona == null) {
            HttpUtil.sendError(exchange, 400, "Zona no encontrada: " + body.get("zonaId"));
            return;
        }
        Atraccion a = new Atraccion(
            body.get("id"),
            body.get("nombre"),
            TipoAtraccion.valueOf(body.getOrDefault("tipo", "MECANICA")),
            Integer.parseInt(body.getOrDefault("capacidadMaximaPorCiclo", "0")),
            Double.parseDouble(body.getOrDefault("alturaMinima", "0")),
            Integer.parseInt(body.getOrDefault("edadMinima", "0")),
            Double.parseDouble(body.getOrDefault("costoAdicional", "0")),
            zona
        );
        service.registrar(a);
        HttpUtil.sendJson(exchange, 201, toJson(a));
    }

    private void buscarPorId(HttpExchange exchange, String id) throws IOException {
        Atraccion a = service.buscarPorId(id);
        if (a == null) { HttpUtil.sendError(exchange, 404, "Atracción no encontrada"); return; }
        HttpUtil.sendJson(exchange, 200, toJson(a));
    }

    private void eliminar(HttpExchange exchange, String id) throws IOException {
        if (!service.eliminar(id)) {
            HttpUtil.sendError(exchange, 404, "Atracción no encontrada");
        } else {
            HttpUtil.sendJson(exchange, 200, "{\"eliminado\":true}");
        }
    }

    private void cambiarEstado(HttpExchange exchange, String id) throws IOException {
        Map<String, String> body = HttpUtil.parseBody(exchange);
        EstadoAtraccion estado = EstadoAtraccion.valueOf(body.getOrDefault("estado", "ACTIVA"));
        String motivo = body.getOrDefault("motivo", "");
        service.cambiarEstado(id, estado, motivo);
        HttpUtil.sendJson(exchange, 200, "{\"ok\":true}");
    }

    static String toJson(Atraccion a) {
        return String.format(Locale.ROOT,
            "{\"id\":\"%s\",\"nombre\":\"%s\",\"tipo\":\"%s\",\"capacidadMaximaPorCiclo\":%d," +
            "\"alturaMinima\":%.2f,\"edadMinima\":%d,\"costoAdicional\":%.2f,\"contadorAcumulado\":%d," +
            "\"tiempoEsperaEstimado\":%d,\"estado\":\"%s\",\"motivoCierre\":%s,\"zona\":%s}",
            HttpUtil.escape(a.getId()), HttpUtil.escape(a.getNombre()), a.getTipo(),
            a.getCapacidadMaximaPorCiclo(), a.getAlturaMinima(), a.getEdadMinima(),
            a.getCostoAdicional(), a.getContadorAcumulado(), a.getTiempoEsperaEstimado(),
            a.getEstado(),
            a.getMotivoCierre() != null ? "\"" + HttpUtil.escape(a.getMotivoCierre()) + "\"" : "null",
            ZonaController.toJson(a.getZona())
        );
    }
}
