import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.server.ServerNotActiveException;

public class ClientHTTP{
	//passer en argument l'IP de la machine hébergeant le service
	public static void main(String[] args) throws RemoteException, NotBoundException, ServerNotActiveException {
		try{
			//Récupérer l'annuaire
			Registry reg = LocateRegistry.getRegistry(args[0], 1099);
			/*
			* Créer une instance de l'objet du service
			* Récupérer le service grace à son nom dans l'annuaire avec la méthode lookup
			*/
			HTTPService hs = (HTTPService) reg.lookup("http");
			/*Lancer le service*/
			System.out.println(hs.fetchData("https://carto.g-ny.org/data/cifs/cifs_waze_v2.json"));
			//System.out.println("Maintenant on essaye de faire une réservation : ");
			//boolean response = rs.makeReservation("Titouan", "LETONDAL", 1, "06 07 09 37 39", 1);
			//System.out.println(response);
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
