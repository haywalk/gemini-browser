package network;

/**
 * Parses and stores URLs.
 * 
 * @author Hayden Walker
 * @version 2024-02-27
 */
public class URL {
    /**
     * Regular expression to match Gemini URLs.
     */
    private static final String URL_FORMAT = "^gemini:\\/\\/[a-zA-Z0-9.]+[\\/[a-zA-Z0-9.]*]*$";

    /**
     * URL prefix.
     */
    private static final String PREFIX = "gemini://";

    /**
     * Store the hostname.
     */
    private String hostname;

    /**
     * Store the URL.
     */
    private String url;

    /**
     * Create a new URL object.
     * 
     * @param url URL.
     * @throws BadURLException If URL format is incorrect.
     * @throws IllegalArgumentException If URL is {@code null}.
     */
    public URL(String url) throws BadURLException, IllegalArgumentException {
        // make sure URL isn't null
        if(url == null) {
            throw new IllegalArgumentException("Illegal argument: null");
        }

        // put into lowercase
        url = url.toLowerCase();

        // check validity
        if(!url.matches(URL_FORMAT)) {
            throw new BadURLException("Invalid URL format.");
        }

        // save url
        this.url = url;

        // parse hostname from URL
        hostname = "";
        int index = PREFIX.length();
        while(index < url.length() && url.charAt(index) != '/') {
            hostname += url.charAt(index++);
        }
    }

    /**
     * Return the hostname.
     * 
     * @return The hostname.
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Return the URL.
     * 
     * @return The URL.
     */
    public String getURL() {
        return url;
    }
}
