import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class LancerProxy {
    public static void main(String[] args) throws IOException {
        // Créer un serveur HTTP écoutant sur le port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        // Définir le contexte et le gestionnaire pour l'endpoint "/restaurants"
        //server.createContext("/restaurants", new RestaurantHandler());
        server.createContext("/incidents", new IncidentsHandler());
        server.createContext("/test", new test());
        // Démarrer le serveur
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");
    }
}
