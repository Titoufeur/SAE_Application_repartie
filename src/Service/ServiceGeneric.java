import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServiceGeneric implements GenericService {

    private final HttpClient httpClient;

    public ServiceGeneric() {
        this.httpClient = HttpClient.newBuilder().build();
    }

    public String fetchData(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
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
