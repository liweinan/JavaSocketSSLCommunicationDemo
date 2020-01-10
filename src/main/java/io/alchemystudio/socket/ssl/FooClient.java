package io.alchemystudio.socket.ssl;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


public class FooClient {
    public static void main(String[] args) throws Exception {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(new HttpGet("https://as1.io"));
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("::: " + statusCode);
    }
}
