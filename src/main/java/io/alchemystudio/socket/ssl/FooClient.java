package io.alchemystudio.socket.ssl;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;


public class FooClient {
    public static void main(String[] args) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

        InputStream certstream = Thread.currentThread().getContextClassLoader().getResourceAsStream("as.ks");
        try {
            trustStore.load(certstream, "XXXXXX".toCharArray());
        } finally {
            certstream.close();
        }

        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(trustStore, new TrustSelfSignedStrategy())
                .build();

        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext) {

            @Override
            public Socket connectSocket(
                    int connectTimeout,
                    Socket socket,
                    HttpHost host,
                    InetSocketAddress remoteAddress,
                    InetSocketAddress localAddress,
                    HttpContext context) throws IOException, ConnectTimeoutException {
                if (socket instanceof SSLSocket) {
                    try {
                        PropertyUtils.setProperty(socket, "host", host.getHostName());
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                    }
                }
                return super.connectSocket(connectTimeout, socket, host, remoteAddress,
                        localAddress, context);
            }

        };

        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(factory).build();
        HttpResponse response = client.execute(new HttpGet("https://as1.io"));
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("::: " + statusCode);
        System.out.println(new BufferedReader(new InputStreamReader(response.getEntity().getContent())).readLine());
    }
}
