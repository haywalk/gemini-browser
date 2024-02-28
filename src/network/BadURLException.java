package network;

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
