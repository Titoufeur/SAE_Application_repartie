import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
                String response = sh.fetchData();

                // Spécifier l'encodage UTF-8 dans l'en-tête de la réponse sinon ça met des caractères point d'interrogation
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "application/json; charset=UTF-8");

                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

                // Envoyer la réponse avec la longueur correcte
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1); // Indicate that the response length is unknown
                exchange.getResponseBody().close();
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Méthode non autorisée
            exchange.getResponseBody().close();
        }
    }
}
