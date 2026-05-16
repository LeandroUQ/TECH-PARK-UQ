package util;

import com.sun.net.httpserver.HttpExchange;

public class CorsUtil {

    public static void addHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:5173");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    // Devuelve true si la petición era preflight (OPTIONS) y ya fue respondida
    public static boolean handlePreflight(HttpExchange exchange) throws java.io.IOException {
        addHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }
}
