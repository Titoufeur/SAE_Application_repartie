import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class serviceHTTP implements HTTPService {

    private final HttpClient httpClient;
    private final String url;

    public serviceHTTP(String url) {
        this.url = url; // Assigner la valeur du paramètre url à this.url
        this.httpClient = HttpClient.newBuilder().build();
    }

    public String fetchData() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.url))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            return response.body();
        } else {
            throw new IOException("HTTP Error: " + statusCode);
        }
    }
}
