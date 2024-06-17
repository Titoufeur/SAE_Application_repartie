import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

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
            System.out.println("Connexion réussie à la BD");
        } else {
            System.out.println("Échec de la connexion à la BD");
        }
        return connection;
    }

    public String getAllRestaurants() throws RemoteException {
        List<Restaurant> restaurants = new ArrayList<>();
        try {
            Connection conn = connect();
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
            throw new RemoteException("Erreur de base de données.");
        }

        JSONArray jsonArray = new JSONArray();
        for (Restaurant restaurant : restaurants) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", restaurant.getId());
            jsonObject.put("name", restaurant.getName());
            jsonObject.put("address", restaurant.getAddress());
            jsonObject.put("gpsCoordinates", restaurant.getGpsCoordinates());
            jsonArray.put(jsonObject);
        }

        return jsonArray.toString();
    }

    public boolean makeReservation(String firstName, String lastName, int numGuests, String phone, int restaurantId) throws RemoteException {
        String sql = "INSERT INTO RESERVATIONS (id, first_name, last_name, num_guests, phone, restaurant_id) VALUES (reservations_seq.NEXTVAL, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setInt(3, numGuests);
            pstmt.setString(4, phone);
            pstmt.setInt(5, restaurantId);
            pstmt.executeUpdate();
            System.out.println("Réservation enregistrée dans la base de données.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la réservation.");
            throw new RemoteException("Erreur de base de données.");
        }
    }
}
