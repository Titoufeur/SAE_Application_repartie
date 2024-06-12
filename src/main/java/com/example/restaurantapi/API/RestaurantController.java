import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.Naming;
import java.rmi.RemoteException;

@RestController
public class RestaurantController {

    private RestaurantService restaurantService;

    public RestaurantController() {
        try {
            // Récupérer le service RMI
            restaurantService = (RestaurantService) Naming.lookup("//localhost:1099/restaurants");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/restaurants")
    public String getRestaurants() {
        try {
            return restaurantService.getAllRestaurants();
        } catch (RemoteException e) {
            e.printStackTrace();
            return "{\"error\": \"Erreur lors de la récupération des données\"}";
        }
    }
}
