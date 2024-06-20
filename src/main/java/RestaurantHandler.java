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
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT"); // Méthodes autorisées
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization"); // En-têtes autorisés
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }
        //Traitement de la requête GET
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                // on récupère les restaurants depuis le service
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                RestaurantService sr = (RestaurantService) registry.lookup("restaurants");
                String response = sr.getAllRestaurants();
                // On renvoie la réponse
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }
            //Traitement de la requête POST
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
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
                        JSONObject jsonResponse = new JSONObject();//On crée un objet json pour renvoyer la réponse dans un bon format
                        jsonResponse.put("status", "Réservation réussie");
                        jsonResponse.put("firstName", firstName);
                        jsonResponse.put("lastName", lastName);
                        byte[] responseBytes = jsonResponse.toString().getBytes(StandardCharsets.UTF_8);
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, responseBytes.length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(responseBytes);
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
            //Traitement de la requête PUT
        } else if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                //On récupère le contenu de la requête afin de pouvoir la traiter facilement
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
                //On récup une String contenant le corps de la requete
                String requestBodyString = requestBody.toString();
                try {
                    //On crée un objet JSON pour extraire chaque attribut
                    JSONObject jsonRequest = new JSONObject(requestBodyString);

                    String name = jsonRequest.getString("name");
                    String address = jsonRequest.getString("address");
                    String gpsCoordinates = jsonRequest.getString("gpsCoordinates");
                    //On récupère l'annuaire local pour récupérer le service
                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    RestaurantService sr = (RestaurantService) registry.lookup("restaurants");
                    //Puis on utilise la méthode du service pour ajouter un restaurant à la base de données
                    boolean res = sr.addRestaurant(name, address, gpsCoordinates);
                    if (res) {
                        //On construit maintenant l'objet JSON qu'on renvoie au client
                        JSONObject jsonResponse = new JSONObject();
                        jsonResponse.put("status", "Restaurant ajouté avec succès");
                        jsonResponse.put("name", name);
                        jsonResponse.put("address", address);
                        byte[] responseBytes = jsonResponse.toString().getBytes(StandardCharsets.UTF_8);
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, responseBytes.length);
                        OutputStream os = exchange.getResponseBody();
                        //Puis on renvoie la réponse disant que la procédure est réalisée avec succès.
                        os.write(responseBytes);
                        os.close();
                    } else {
                        exchange.sendResponseHeaders(500, 0);
                        exchange.getResponseBody().close();
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
        } else {
            exchange.sendResponseHeaders(405, 0);
            exchange.getResponseBody().close();
        }
    }
}
