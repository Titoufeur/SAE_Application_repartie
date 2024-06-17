import java.io.Serializable;

public class Restaurant implements Serializable {
    private int id;
    private String name;
    private String address;
    private String gpsCoordinates;

    public Restaurant(int id, String name, String address, String gpsCoordinates) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.gpsCoordinates = gpsCoordinates;
    }

    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", gpsCoordinates='" + gpsCoordinates + '\'' +
                '}';
    }

    public String toJson() {
       StringBuilder json = new StringBuilder();
       json.append("{");
       json.append("\"id\":").append(id).append(",");
       json.append("\"name\":\"").append(name).append("\",");
       json.append("\"address\":\"").append(address).append("\",");
       json.append("\"gpsCoordinates\":\"").append(gpsCoordinates).append("\"");
       json.append("}");
       return json.toString();
   }

   public String getName() {return this.name;}
    public String getAddress() {return this.address;}
    public String getGpsCoordinates() {return this.gpsCoordinates;}
    public int getId() {return this.id;}
}
