//package Service;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RestaurantService extends Remote {
    String getAllRestaurants() throws RemoteException;
    boolean makeReservation(String firstName, String lastName, int numPersons, String contactNumber, int time) throws RemoteException;
}