import java.io.IOException;
import java.rmi.Remote;

public interface GenericService extends Remote {
    public String fetchData(String url) throws IOException, InterruptedException;
}