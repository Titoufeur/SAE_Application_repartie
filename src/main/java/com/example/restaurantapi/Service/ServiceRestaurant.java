import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRestaurant implements RestaurantService {

  private String motdepasse;
  private String identifiant;


  public ServiceRestaurant(String identifiant, String motdepasse){
    this.motdepasse = motdepasse;
    this.identifiant = identifiant;
  }

  public Connection connect() throws SQLException {
    try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
      Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb", this.identifiant, this.motdepasse);
      if (connection != null) {
          System.out.println("Connexion reussie a la base de donnees !");
      } else {
          System.out.println("Échec de la connexion à la base de données !");
      }
      return connection;
  }

    public String getAllRestaurants() throws RemoteException {
        List<Restaurant> restaurants = new ArrayList<>();
        try{
          Connection conn = connect();
          // Mets l'autocommit à false
          connect().setAutoCommit(false);
          // Pose un verrou sur la table restaurant
          connect().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

          Statement stmt = conn.createStatement();
          String sql = "SELECT * FROM RESTAURANTS";
          ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                String gpsCoordinates = rs.getString("gps_coordinates");
                System.out.println(id + " | " + name + " | " + address + " | " + gpsCoordinates);
                restaurants.add(new Restaurant(id, name, address, gpsCoordinates));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Database error.");
        }
        System.out.println("Restaurants retournes : ");
        System.out.println(restaurantsListToJson(restaurants));
        return restaurantsListToJson(restaurants);
    }

    public boolean makeReservation(String firstName, String lastName, int numGuests, String phone, int restaurantId) throws RemoteException {
        String sql = "INSERT INTO RESERVATIONS (id, first_name, last_name, num_guests, phone, restaurant_id) VALUES (reservations_seq.NEXTVAL, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = connect();
            // Mets l'autocommit à false
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setInt(3, numGuests);
            pstmt.setString(4, phone);
            pstmt.setInt(5, restaurantId);
            pstmt.executeUpdate();
            System.out.println("la réservation est bien enregistré dans la base de donnée");
            // Valide la modification
            conn.commit();
            return true;
        } catch (SQLException e) {
            // Annule la mise à jour
            conn.rollback();
            e.printStackTrace();
            System.out.println("Erreur lors de la tentative de réservation");
            throw new RemoteException("Database error.");
        }
    }

    public static String restaurantsListToJson(List<Restaurant> restaurants) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < restaurants.size(); i++) {
            json.append(restaurants.get(i).toJson());
            if (i < restaurants.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

}
