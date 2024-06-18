//package Service;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

//Lancer sur windows Powershell : java -cp '.;ojdbc11.jar' LancerService username password (username et password de la base de données SQL Developer)
public class LancerService {
	public static void main(String[] args){
		ServiceRestaurant sr = new ServiceRestaurant(args[0], args[1]);
		serviceHTTP sh = new serviceHTTP("https://carto.g-ny.org/data/cifs/cifs_waze_v2.json");
		try {
			//Exporter les instances
			RestaurantService rs = (RestaurantService) UnicastRemoteObject.exportObject(sr, 0);
			HTTPService shttp = (HTTPService) UnicastRemoteObject.exportObject(sh, 0);
			//Récupérer l'annunaire
			Registry reg = LocateRegistry.getRegistry();
			//Enregistrer les services dans l'annuaire avec un nom
			reg.rebind("restaurants", rs);
			System.out.println("Service restaurant démarré avec succès !");
			reg.rebind("http", shttp);
			System.out.println("Service HTTP démarré avec succès !");
		} catch(RemoteException rm){
			rm.printStackTrace();
		}
	}
}