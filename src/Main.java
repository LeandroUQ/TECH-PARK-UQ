import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.IOException;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Registra aquí los handlers por ruta
        // server.createContext("/api/ejemplo", new EjemploController());

        server.setExecutor(null);
        server.start();
        System.out.println("Servidor corriendo en http://localhost:" + PORT);
    }
}
