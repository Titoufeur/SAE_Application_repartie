import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RestaurantService extends Remote {
    String getAllRestaurants() throws RemoteException;
    boolean makeReservation(String firstName, String lastName, int numGuests, String phone, int restaurantId) throws RemoteException;
}
