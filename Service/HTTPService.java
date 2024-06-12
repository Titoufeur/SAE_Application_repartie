import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HTTPService extends Remote{
    String fetchData(String url) throws IOException, InterruptedException, URISyntaxException;
}
