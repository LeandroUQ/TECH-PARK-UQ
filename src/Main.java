import com.sun.net.httpserver.HttpServer;
import controller.AtraccionController;
import controller.ColaController;
import controller.PersonalController;
import controller.TicketController;
import controller.VisitanteController;
import controller.ZonaController;
import java.net.InetSocketAddress;
import java.io.IOException;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/visitantes",  new VisitanteController());
        server.createContext("/api/atracciones", new AtraccionController());
        server.createContext("/api/zonas",       new ZonaController());
        server.createContext("/api/tickets",     new TicketController());
        server.createContext("/api/cola",        new ColaController());
        server.createContext("/api/personal",    new PersonalController());

        server.setExecutor(null);
        server.start();
        System.out.println("Servidor corriendo en http://localhost:" + PORT);
    }
}
