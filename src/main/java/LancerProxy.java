import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class LancerProxy {
    public static void main(String[] args) throws IOException {
        // on crée un serveur HTTP écoutant sur le port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(Integer.parseInt(args[0])), 0);

        // On crée les endpoints et on associe une classe à chacun d'entre eux
        server.createContext("/restaurants", new RestaurantHandler());
        server.createContext("/fetch", new GenericHandler());

        // et on démarre le serveur
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + args[0]);
    }
}
