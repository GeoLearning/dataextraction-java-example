package com.geolearning;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ResourceTest {
    private static final String URL = "http://localhost/dataextraction";
    private Resource resource;

    @Mock
    private HttpConnectionFactory httpConnectionFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        resource = new Resource(URL, "username", "password");
        resource.setHttpConnectionFactory(httpConnectionFactory);
    }

    @Test
    public void testThatDefaultAuthenticatorIsSet() {
        PasswordAuthentication authentication = Authenticator.requestPasswordAuthentication(null, 80, null, null, null);
        assertEquals("username", authentication.getUserName());
        assertEquals("password", new String(authentication.getPassword()));
    }

    @Test
    public void testGetCsv() throws Exception {
        ByteArrayOutputStream postData = new ByteArrayOutputStream();
        HttpURLConnection postConnection = mock(HttpURLConnection.class);
        when(httpConnectionFactory.newConnectionFor(new URL(URL + "/users"))).thenReturn(postConnection);
        when(postConnection.getOutputStream()).thenReturn(postData);
        when(postConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_CREATED);
        when(postConnection.getHeaderField("Location")).thenReturn(URL + "/thefile.csv");

        HttpURLConnection getConnection = mock(HttpURLConnection.class);
        when(httpConnectionFactory.newConnectionFor(new URL(URL + "/thefile.csv"))).thenReturn(getConnection);
        when(getConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(getConnection.getInputStream()).thenReturn(new ByteArrayInputStream("column_1,column_2\nfoo,bar".getBytes()));

        Csv csv = resource.getCsvFor("users");
        assertNotNull(csv);
        assertEquals(1, csv.size());
        assertEquals("foo", csv.row(0).get("column_1"));

        verify(postConnection).setRequestMethod("POST");
        verify(postConnection).setDoOutput(true);
        verify(postConnection).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        assertEquals("header_required=true", postData.toString());
        verify(getConnection).setRequestMethod("GET");
    }
}
