import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class serviceHTTP implements HTTPService {

    private final HttpClient httpClient;

    /*public serviceHTTP(String proxyHost, int proxyPort) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        this.httpClient = HttpClient.newBuilder().proxy(HttpClient.Builder.NO_PROXY).build();
    }*/

    public serviceHTTP() {
        this.httpClient = HttpClient.newBuilder().build();
    }

    @Override
    public String fetchData(String url) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            return response.body();
        } else {
            throw new IOException("HTTP Error: " + statusCode);
        }
    }
}
