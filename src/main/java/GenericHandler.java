import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

class GenericHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Permettre l'accès depuis n'importe quelle origine
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS"); // Méthodes autorisées
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization"); // En-têtes autorisés
        if ("GET".equals(exchange.getRequestMethod())) {
            //On regarde si un paramètre a été passé (l'url donc)
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.startsWith("url=")) {
                String urlString = query.substring(4);
                //Si c'est le cas, on va alors récupérer le service afin de fetch

                try {
                    // On récupère l'annuaire
                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    // On récupère le service
                    GenericService sg = (GenericService) registry.lookup("generic");
                    //On fetch avec l'URL demandé.
                    String response = sg.fetchData(urlString);

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
                    exchange.sendResponseHeaders(500, -1);
                    exchange.getResponseBody().close();
                }
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
            exchange.getResponseBody().close();
        }
    }
}
