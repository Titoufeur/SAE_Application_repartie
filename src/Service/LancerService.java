//package Service;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

//Lancer sur windows Powershell : java -cp '.;ojdbc11.jar' LancerService username password (username et password de la base de données SQL Developer)
public class LancerService {
	public static void main(String[] args){
		ServiceRestaurant sr = new ServiceRestaurant(args[0], args[1]);
		ServiceGeneric sg = new ServiceGeneric();
		try {
			//Exporter les instances
			RestaurantService rs = (RestaurantService) UnicastRemoteObject.exportObject(sr, 0);
			GenericService gs = (GenericService) UnicastRemoteObject.exportObject(sg, 0);
			//Récupérer l'annunaire
			Registry reg = LocateRegistry.getRegistry();
			//Enregistrer les services dans l'annuaire avec un nom
			reg.rebind("restaurants", rs);
			System.out.println("Service restaurant démarré avec succès !");
			reg.rebind("generic", gs);
			System.out.println("Service générique démarré avec succès !");
		} catch(RemoteException rm){
			rm.printStackTrace();
		}
	}
}