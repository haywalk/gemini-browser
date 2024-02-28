package network;

/**
 * Thrown when a URL format is invalid.
 * 
 * @author Hayden Walker
 * @version 2024-02-27
 */
public class BadURLException extends RuntimeException {
    /**
     * Create a new BadURLException.
     * 
     * @param message Error message.
     */
    public BadURLException(String message) {
        super(message);
    }
}
