package Client;
import Service.RestaurantService;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: java Client <server_ip>");
			return;
		}

		try {
			Registry reg = LocateRegistry.getRegistry(args[0], 1099);
			RestaurantService rs = (RestaurantService) reg.lookup("restaurants");

			System.out.println(rs.getAllRestaurants());
			System.out.println("Essai de faire une réservation : ");
			boolean response = rs.makeReservation("Bastien", "Jallais", 1, "06 06 06 06 06", 6);
			System.out.println(response);
		} catch (Exception e) {
			System.err.println("Erreur client : " + e.getMessage());
			e.printStackTrace();
		}
	}
}