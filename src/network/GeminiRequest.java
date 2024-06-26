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
import java.security.SecureRandom;

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
     * Content type returned by the server.
     */
    private String type;

    /**
     * URL
     */
    private String url;

    /**
     * Status returned by the server.
     */
    private int status;

    /**
     * Create a new Gemini request.
     * 
     * @param host Server to send request to.
     * @param url Resource to request.
     * @throws RequestFailedException If the server rejects the request or any other issue is encountered.
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

            // read the responseRequestFailedException
            byte[] returned = in.readAllBytes();

            // parse content returned and close the socket
            parseContent(returned);
            socket.close();
        } 
        // failed to send request or receive response
        catch(IOException e) {
            throw new RequestFailedException("Failed to communicate with the server.");
        }

        this.url = url;
    }

    /**
     * Create a new Gemini request.
     * 
     * @param url URL (gemini://hostname/resource) to request.
     */
    public GeminiRequest(URL url) {
        this(url.getHostname(), url.getURL());
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
        return status;
    }

    /**
     * Return the type of content returned by the server.
     * 
     * @return The type of content returned by the server.
     */
    public String getHeaderInfo() {
        return type;
    }

    /**
     * Create an SSL context. For simplicity, this will accept any server certificate.
     * 
     * Adapted from: https://stackoverflow.com/questions/1219208/is-it-possible-to-get-java-to-ignore-the-trust-store-and-just-accept-whatever
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
                        // this method is empty on purpose
                } 
                public void checkServerTrusted( 
                    java.security.cert.X509Certificate[] certs, String authType) {
                        // this method is empty on purpose
                }
            } 
        }; 
        
        // initialize and return the SSL context
        context.init(null, trustAllCerts, new SecureRandom()); 
        return context;
    }

    /**
     * Given the raw data returned by the server, parse the header and content.
     * 
     * @param returned Bytes returned by the server.
     */
    private void parseContent(byte[] returned) {
        // initialize instance variables to default values
        status = 0;
        type = "";
        content = null;

        int index = 0; // start parsing at first byte

        // parse the status
        for(int i = 0; i < STATUS_DIGITS; i++) {
            status *= 10;
            status += (returned[index++] - '0');
        }

        // if there is additional header information, skip over the space
        // between the status and that information
        if(returned[index] != '\r') {
            index++;
        }

        // parse the content type
        StringBuilder contentType = new StringBuilder();
        while((char) returned[index] != '\r') { // advance until carriage return
            contentType.append((char) returned[index++]);
        }
        type = contentType.toString();
        
        index++; // jump over the carriage return character
        index++; // jump over the line feed character

        // load the content
        content = new Byte[returned.length - index];
        for(int i = 0; i < content.length; i++) {
            content[i] = returned[index++];
        }
    }
    
    /**
     * Get the URL for this request.
     * 
     * @return URL.
     */
    public String url() {
        return this.url;
    }
}
