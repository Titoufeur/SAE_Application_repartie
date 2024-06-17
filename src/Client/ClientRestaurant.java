package Client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/*
* Cette classe se lancer lorsque la fonction fetchRestaurant de index.js se lance
* Elle demande alors au service de récupérer les informations sur la base de donnée
*/
public class ClientRestaurant {
    public static void main(String[] args) {
        try {
            // Adresse de votre API REST
            URL url = new URL("http://localhost:8080/restaurants");

            // Ouvrir la connexion HTTP
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Spécifier la méthode de requête (GET dans ce cas)
            conn.setRequestMethod("GET");

            // Lire la réponse de l'API
            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // Afficher la réponse (les données des restaurants)
            System.out.println(response.toString());

            // Fermer la connexion
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
