package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Administrador;
import model.Operador;
import model.Personal;
import model.Zona;
import service.PersonalService;
import service.ZonaService;
import util.CorsUtil;
import util.HttpUtil;

import java.io.IOException;
import java.util.Map;

public class PersonalController implements HttpHandler {

    private static final String PREFIX = "/api/personal";
    private final PersonalService service = PersonalService.getInstance();
    private final ZonaService zonaService = ZonaService.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (CorsUtil.handlePreflight(exchange)) return;
        CorsUtil.addHeaders(exchange);
        String method = exchange.getRequestMethod().toUpperCase();
        String extra = exchange.getRequestURI().getPath().substring(PREFIX.length());

        try {
            if (extra.isEmpty() || extra.equals("/")) {
                if ("GET".equals(method)) listarTodos(exchange);
                else HttpUtil.sendError(exchange, 405, "Método no permitido");
            } else {
                String segment = extra.substring(1);
                switch (segment) {
                    case "operadores":
                        if ("GET".equals(method)) listarOperadores(exchange);
                        else HttpUtil.sendError(exchange, 405, "Método no permitido");
                        break;
                    case "administradores":
                        if ("GET".equals(method)) listarAdministradores(exchange);
                        else HttpUtil.sendError(exchange, 405, "Método no permitido");
                        break;
                    case "operador":
                        if ("POST".equals(method)) registrarOperador(exchange);
                        else HttpUtil.sendError(exchange, 405, "Método no permitido");
                        break;
                    case "administrador":
                        if ("POST".equals(method)) registrarAdministrador(exchange);
                        else HttpUtil.sendError(exchange, 405, "Método no permitido");
                        break;
                    default:
                        if ("GET".equals(method)) buscarPorId(exchange, segment);
                        else if ("DELETE".equals(method)) eliminar(exchange, segment);
                        else HttpUtil.sendError(exchange, 405, "Método no permitido");
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
        for (Personal p : service.listarTodos()) {
            if (!first) sb.append(",");
            sb.append(toJson(p));
            first = false;
        }
        HttpUtil.sendJson(exchange, 200, sb.append("]").toString());
    }

    private void listarOperadores(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Operador op : service.listarOperadores()) {
            if (!first) sb.append(",");
            sb.append(toJson(op));
            first = false;
        }
        HttpUtil.sendJson(exchange, 200, sb.append("]").toString());
    }

    private void listarAdministradores(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Administrador adm : service.listarAdministradores()) {
            if (!first) sb.append(",");
            sb.append(toJson(adm));
            first = false;
        }
        HttpUtil.sendJson(exchange, 200, sb.append("]").toString());
    }

    private void registrarOperador(HttpExchange exchange) throws IOException {
        Map<String, String> body = HttpUtil.parseBody(exchange);
        Zona zona = zonaService.buscarPorId(body.get("zonaId"));
        if (zona == null) {
            HttpUtil.sendError(exchange, 400, "Zona no encontrada: " + body.get("zonaId"));
            return;
        }
        Operador op = new Operador(body.get("id"), body.get("nombre"), body.get("documento"), zona);
        service.registrar(op);
        HttpUtil.sendJson(exchange, 201, toJson(op));
    }

    private void registrarAdministrador(HttpExchange exchange) throws IOException {
        Map<String, String> body = HttpUtil.parseBody(exchange);
        Administrador adm = new Administrador(body.get("id"), body.get("nombre"), body.get("documento"));
        service.registrar(adm);
        HttpUtil.sendJson(exchange, 201, toJson(adm));
    }

    private void buscarPorId(HttpExchange exchange, String id) throws IOException {
        Personal p = service.buscarPorId(id);
        if (p == null) { HttpUtil.sendError(exchange, 404, "Personal no encontrado"); return; }
        HttpUtil.sendJson(exchange, 200, toJson(p));
    }

    private void eliminar(HttpExchange exchange, String id) throws IOException {
        if (!service.eliminar(id)) {
            HttpUtil.sendError(exchange, 404, "Personal no encontrado");
        } else {
            HttpUtil.sendJson(exchange, 200, "{\"eliminado\":true}");
        }
    }

    private static String toJson(Personal p) {
        String base = String.format("{\"id\":\"%s\",\"nombre\":\"%s\",\"documento\":\"%s\"",
            HttpUtil.escape(p.getId()), HttpUtil.escape(p.getNombre()), HttpUtil.escape(p.getDocumento()));
        if (p instanceof Operador) {
            Operador op = (Operador) p;
            return base + String.format(",\"tipo\":\"OPERADOR\",\"zonaAsignada\":%s}",
                ZonaController.toJson(op.getZonaAsignada()));
        }
        return base + ",\"tipo\":\"ADMINISTRADOR\"}";
    }
}
