import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

class RestaurantHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            try {
                // On récupère l'annuaire
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                // On récupère le service
                RestaurantService sr = (RestaurantService) registry.lookup("restaurant");

                String response = sr.getAllRestaurants();

                // Envoyer la réponse
                exchange.sendResponseHeaders(200, response.getBytes().length);
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }
        } else {
            exchange.sendResponseHeaders(405, 0); // Méthode non autorisée
            exchange.getResponseBody().close();
        }
    }
}