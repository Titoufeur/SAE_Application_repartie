import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

class RestaurantHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Permettre l'accès depuis n'importe quelle origine
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS"); // Méthodes autorisées
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization"); // En-têtes autorisés
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                // Récupérer les restaurants depuis le service
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                RestaurantService sr = (RestaurantService) registry.lookup("restaurants");
                String response = sr.getAllRestaurants();
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            System.out.println("Requete POST recue !!!!!!!!!!!!");
            try {
                StringBuilder requestBody = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        requestBody.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, 0);
                    exchange.getResponseBody().close();
                    return;
                }

                String requestBodyString = requestBody.toString();
                System.out.println(requestBodyString);
                String restaurantName;
                try {
                    JSONObject jsonRequest = new JSONObject(requestBodyString);

                    String firstName = jsonRequest.getString("firstName");
                    String lastName = jsonRequest.getString("lastName");
                    int numGuests = jsonRequest.getInt("numGuests");
                    String phone = jsonRequest.getString("phone");
                    int restaurantId = jsonRequest.getInt("restaurantId");
                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    RestaurantService sr = (RestaurantService) registry.lookup("restaurants");
                    boolean res = sr.makeReservation(firstName, lastName, numGuests, phone, restaurantId);
                    if (res){
                        String responseMessage = "Réservation réussie pour " + firstName + " " + lastName;
                        exchange.sendResponseHeaders(200, responseMessage.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(responseMessage.getBytes());
                        os.close();
                    } else{
                        exchange.sendResponseHeaders(500, 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(400, 0);
                    exchange.getResponseBody().close();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }
        }

        else {
            exchange.sendResponseHeaders(405, 0);
            exchange.getResponseBody().close();
        }
    }
}
