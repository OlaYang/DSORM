package com.meiqi.openservice.commons.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

/**
 * <class description>
 *      
 * @author: luzicong
 * @version: 1.0, 2015年8月21日
 */
public class SimpleSSLSocketFactory extends SSLSocketFactory {

    private static final Logger Log = Logger.getLogger(SimpleSSLSocketFactory.class);

    private SSLSocketFactory    factory;



    public SimpleSSLSocketFactory() {

        try {
            String algorithm = "TLS";
            SSLContext sslcontent = SSLContext.getInstance(algorithm);
            sslcontent.init(null, // KeyManager not required
                    new TrustManager[] { new DummyTrustManager() }, new java.security.SecureRandom());
            factory = sslcontent.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            Log.error(e.getMessage(), e);
        } catch (KeyManagementException e) {
            Log.error(e.getMessage(), e);
        }
    }



    public static SocketFactory getDefault() {
        return new SimpleSSLSocketFactory();
    }



    @Override
    public Socket createSocket() throws IOException {
        return factory.createSocket();
    }



    @Override
    public Socket createSocket(Socket socket, String s, int i, boolean flag) throws IOException {
        return factory.createSocket(socket, s, i, flag);
    }



    @Override
    public Socket createSocket(InetAddress inaddr, int i, InetAddress inaddr2, int j) throws IOException {
        return factory.createSocket(inaddr, i, inaddr2, j);
    }



    @Override
    public Socket createSocket(InetAddress inaddr, int i) throws IOException {
        return factory.createSocket(inaddr, i);
    }



    @Override
    public Socket createSocket(String s, int i, InetAddress inaddr, int j) throws IOException {
        return factory.createSocket(s, i, inaddr, j);
    }



    @Override
    public Socket createSocket(String s, int i) throws IOException {
        return factory.createSocket(s, i);
    }



    @Override
    public String[] getDefaultCipherSuites() {
        return factory.getSupportedCipherSuites();
    }



    @Override
    public String[] getSupportedCipherSuites() {
        return factory.getSupportedCipherSuites();
    }

    private static class DummyTrustManager implements X509TrustManager {

        public boolean isClientTrusted(X509Certificate[] cert) {
            return true;
        }



        public boolean isServerTrusted(X509Certificate[] cert) {
            try {
                cert[0].checkValidity();
                return true;
            } catch (CertificateExpiredException e) {
                return false;
            } catch (CertificateNotYetValidException e) {
                return false;
            }
        }



        public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s)
                throws CertificateException {
        }



        public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s)
                throws CertificateException {
        }



        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
