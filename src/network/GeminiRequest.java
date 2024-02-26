package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * A Gemini request.
 * 
 * @author Hayden Walker
 * @version 2024-02-26
 */
public class GeminiRequest {
    /**
     * The port used for Gemini requests.
     */
    private static final int GEMINI_PORT = 1965;

    /**
     * End-of-line characters (CRLF).
     */
    private static final String EOL = "\r\n";

    /**
     * Number of digits in status codes.
     */
    private static final int STATUS_DIGITS = 2;

    /**
     * Content returned by the server.
     */
    private Byte[] content;

    /**
     * Create a new Gemini request.
     * 
     * @param host Server to send request to.
     * @param url Resource to request.
     * @throws RequestFailedException
     */
    public GeminiRequest(String host, String url) throws RequestFailedException {

        SSLContext context;
        SSLSocket socket;

        // attempt to create the SSL context
        try {
            context = createSSLContext();
        } 
        // failed to create SSL context
        catch(KeyManagementException | NoSuchAlgorithmException e) {
            throw new RequestFailedException("Failed to create SSL context.");
        }

        // attempt to open the SSL client socket
        try {
            SSLSocketFactory factory = context.getSocketFactory();
            socket = (SSLSocket) factory.createSocket(host, GEMINI_PORT);        
        } 
        // failed: unknown host
        catch(UnknownHostException e) {
            throw new RequestFailedException("Unknown host: " + host);
        } 
        // failed: other reason
        catch(IOException e) {
            throw new RequestFailedException("Failed to open SSL socket.");
        }

        // attempt to send request and read response
        try {
            socket.startHandshake(); // start handshake with server

            // get input/output streamse
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // send the request
            out.write((url + EOL).getBytes());

            // read the response
            byte[] returned = in.readAllBytes();

            // jump through hoops to cast to Byte[] array
            content = new Byte[returned.length];
            for(int i = 0; i < returned.length; i++) {
                content[i] = returned[i];
            }
        } 
        // failed to send request or receive response
        catch(IOException e) {
            throw new RequestFailedException("Failed to communicate with the server.");
        }
    }


    /**
     * Return the content returned by the server.
     * 
     * @return The content returned by the server.
     */
    public Byte[] getContent() {
        return content;
    }

    /**
     * Return the status returned by the server.
     * 
     * @return The status returned by the server.
     */
    public int getStatus() {
        int status = 0;
       
        for(int i = 0; i < STATUS_DIGITS; i++) {
            status *= 10;
            status += (content[i] - '0');
        }

        return status;
    }

    /**
     * Create an SSL context. For simplicity, this will accept any server certificate.
     * 
     * @return SSLContext object.
     * @throws KeyManagementException If initialization of SSLContext fails.
     * @throws NoSuchAlgorithmException If SSLContext doesn't support TLS.
     */
    private SSLContext createSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        // create an SSLContext for TLS
        SSLContext context = SSLContext.getInstance("TLS");

        // create a TrustManager that trusts all server certificates.
        TrustManager[] trustAllCerts = new TrustManager[] { 
            new X509TrustManager() {     
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
                    return new X509Certificate[0];
                } 
                public void checkClientTrusted( 
                    java.security.cert.X509Certificate[] certs, String authType) {
                } 
                public void checkServerTrusted( 
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            } 
        }; 
        
        // initialize and return the SSL context
        context.init(null, trustAllCerts, new java.security.SecureRandom()); 
        return context;
    }
}
