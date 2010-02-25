package com.geolearning;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

public class Resource {
    private static final Logger LOG = Logger.getLogger(Resource.class);

    private String baseUrl;
    private HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory();

    public Resource(String baseUrl, final String username, final String password) {
        this.baseUrl = baseUrl;

        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });
    }

    public Csv getCsvFor(String resource) {
        try {
            String location = getFileLocation(resource);
            InputStream content = getFileContents(location);
            return new Csv(content);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getFileLocation(String resource) throws Exception {
        HttpURLConnection post = httpConnectionFactory.newConnectionFor(new URL(baseUrl + "/" + resource));
        LOG.debug("Posting to " + post.getURL());
        post.setRequestMethod("POST");
        post.setDoOutput(true);
        post.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        post.getOutputStream().write("header_required=true".getBytes());
        assertResponseCode(post, HttpURLConnection.HTTP_CREATED);
        return post.getHeaderField("Location");
    }

    private InputStream getFileContents(String location) throws Exception {
        HttpURLConnection get = httpConnectionFactory.newConnectionFor(new URL(location));
        LOG.debug("Getting " + get.getURL());
        get.setRequestMethod("GET");
        assertResponseCode(get, HttpURLConnection.HTTP_OK);
        return get.getInputStream();
    }

    private void assertResponseCode(HttpURLConnection connection, int expected) throws Exception {
        LOG.debug("Received status code: " + connection.getResponseCode());

        if(connection.getResponseCode() != expected) {
            String message = "Unexpected status code for %s: expected %s, received %s";
            throw new RuntimeException(String.format(message, connection.getURL(), expected, connection.getResponseCode()));
        }
    }

    protected void setHttpConnectionFactory(HttpConnectionFactory httpConnectionFactory) {
        this.httpConnectionFactory = httpConnectionFactory;
    }
}
