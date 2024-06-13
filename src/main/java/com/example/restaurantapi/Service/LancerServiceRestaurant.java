import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LancerServiceRestaurant {
    public static void main(String[] args){
        System.out.println("Avant lancement.");
        try {
            ServiceRestaurant sr = new ServiceRestaurant(args[0], args[1]);
            System.out.println("Avant export.");
            RestaurantService rs = (RestaurantService) UnicastRemoteObject.exportObject(sr, 0);

            System.out.println("Après export.");
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind("restaurants", rs);

            System.out.println("Service de restaurant lancé avec succès.");
        } catch(RemoteException rm){
            rm.printStackTrace();
        }
    }
}
