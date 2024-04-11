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
    private static final String URL_FORMAT = "^gemini://[a-zA-Z0-9.]+[/[a-zA-Z0-9.]*]*\\??.*$";

    /**
     * URL prefix.
     */
    private static final String PREFIX = "gemini://";

    /**
     * Store the hostname.
     */
    private String hostname;

    /**
     * Folder on server, e.g. /img/
     */
    private String folder;

    /**
     * File on server, e.g. image.jpg
     */
    private String file;

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
        if(!URL.isValidURL(url)) {
            throw new BadURLException("Invalid URL format.");
        }

        // parse url
        parse(url);
    }


    /**
     * Parse the hostname, folder, and file from a URL.
     * 
     * @param url URL to parse.
     * @return Hostname.
     */
    private void parse(String url) {
        // parse hostname from URL
        hostname = "";
        int index = PREFIX.length(); // skip over gemini://
        while(index < url.length() && url.charAt(index) != '/') {           
            hostname += url.charAt(index++);
        }

        // parse file
        file = "";
        int fileIndex;
        for(fileIndex = url.length() - 1; 
            url.charAt(fileIndex) != '/' && fileIndex > PREFIX.length() + hostname.length() - 1; 
            fileIndex--) {}

        if(++fileIndex < url.length()) {
            file = url.substring(fileIndex);
        }

        // parse folder
        if(index < fileIndex) {
            folder = url.substring(index, fileIndex);
        } else {
            folder = "/";
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
        return PREFIX + hostname + folder + file;
    }

    public String getFile() {
        return file;
    }

    /**
     * Return the URL of the folder this file is in.
     * @return Folder URL
     */
    public String getFolderURL() {
        return PREFIX + hostname + folder;
    }

    /**
     * Check the validity of a URL.
     * 
     * @param url URL to check.
     * @return {@code true} if valid.
     */
    public static boolean isValidURL(String url) {
        return url.matches(URL_FORMAT);
    }

}
