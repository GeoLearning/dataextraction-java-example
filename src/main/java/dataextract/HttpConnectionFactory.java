package dataextract;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnectionFactory {
    public HttpURLConnection newConnectionFor(URL url) {
        try {
            return (HttpURLConnection) url.openConnection();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
