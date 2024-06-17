import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

class IncidentsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        CORSUtil.addCORSHeaders(exchange);
        if ("GET".equals(exchange.getRequestMethod())) {
            try {
                // On récupère l'annuaire
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                // On récupère le service
                HTTPService sh = (HTTPService) registry.lookup("http");
                String urlIncidents = "https://carto.g-ny.org/data/cifs/cifs_waze_v2.json";
                String response = sh.fetchData();
                // Envoyer la réponse
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
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
