package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import network.GeminiRequest;
import network.URL;

/**
 * JUnit tests for GeminiRequest.
 * 
 * @author Hayden Walker
 * @version 2024-02-27
 */
public class TestGeminiRequest {
    /**
     * Test making a bad Gemini request.
     */
    @Test
    public void testBadRequest(){
        // create a bad request
        GeminiRequest badRequest = new GeminiRequest("gemini.haywalk.ca", "zork");
        
        // should return status 59
        assertEquals(badRequest.getStatus(), 59);
    }

    /**
     * Test a successful gemtext request.
     */
    @Test
    public void testGemtextRequest() {
        // create a new request
        GeminiRequest gemtextRequest = new GeminiRequest("gemini.haywalk.ca", "gemini://gemini.haywalk.ca/");

        // should return status 20 and type text/gemini
        assertEquals(gemtextRequest.getStatus(), 20);
        assertEquals(gemtextRequest.getHeaderInfo(), "text/gemini"); 
    }

    /**
     * Test a successful plaintext request.
     */
    @Test
    public void testPlaintextRequest() {
        // create a new request
        GeminiRequest gemtextRequest = new GeminiRequest("gemini.haywalk.ca", "gemini://gemini.haywalk.ca/test.txt");

        // should return status 20 and type text/plain
        assertEquals(gemtextRequest.getStatus(), 20);
        assertEquals(gemtextRequest.getHeaderInfo(), "text/plain"); 
    }

    /**
     * Test a request to gemini.circumlunar.space.
     */
    @Test
    public void testProjectGeminiCapsule() {
        // create a new request
        GeminiRequest gemtextRequest = new GeminiRequest("gemini.circumlunar.space", "gemini://gemini.circumlunar.space/");

        // should return status 20 and type text/gemini
        assertEquals(gemtextRequest.getStatus(), 20);
        assertEquals(gemtextRequest.getHeaderInfo(), "text/gemini"); 
    }

    /**
     * Test a request using a URL object.
     */
    @Test
    public void testURLRequest() {
        // create a new URL and a new GeminiRequest
        URL url = new URL("gemini://gemini.haywalk.ca/oldblog.gmi");
        GeminiRequest urlRequest = new GeminiRequest(url);

        // should return status 20 and type text/gemini
        assertEquals(urlRequest.getStatus(), 20);
        assertEquals(urlRequest.getHeaderInfo(), "text/gemini"); 
    }
}