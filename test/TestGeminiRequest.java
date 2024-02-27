package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import network.GeminiRequest;

/**
 * Junit tests for GeminiRequest
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
        // Create a bad request
        GeminiRequest badRequest = new GeminiRequest("gemini.haywalk.ca", "zork");
        
        // Should return status 59 and type bad request
        assertEquals(badRequest.getStatus(), 59);
        assertTrue(badRequest.getContentType().equals("Bad request"));
    }
}