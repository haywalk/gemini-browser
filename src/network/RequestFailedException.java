package network;

/**
 * Thrown when a fatal error is encountered when making a 
 * Gemini request.
 * 
 * @author Hayden Walker
 * @version 2023-02-26
 */
public class RequestFailedException extends RuntimeException {
    /**
     * Create a new RequestFailedException.
     * 
     * @param message Error message.
     */
    public RequestFailedException(String message) {
        super(message);
    }
}
