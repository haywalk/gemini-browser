package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import network.BadURLException;
import network.URL;

/**
 * JUnit tests for URL.
 * 
 * @author Hayden Walker
 * @version 2024-02-27
 */
public class TestURL {
    /**
     * Test URL's parsing methods.
     */
    @Test
    public void testURLParsing() {
        // test with one URL
        URL url1 = new URL("gemini://gemini.haywalk.ca");
        assertTrue(url1.getHostname().equals("gemini.haywalk.ca"));
        assertTrue(url1.getURL().equals("gemini://gemini.haywalk.ca"));

        // test with another URL
        URL url2 = new URL("gemini://gemini.circumlunar.space/capcom/");
        assertTrue(url2.getHostname().equals("gemini.circumlunar.space"));
        assertTrue(url2.getURL().equals("gemini://gemini.circumlunar.space/capcom/"));
    }

    /**
     * Test URL's validation of URLs.
     */
    @Test
    public void testValidation() {
        // make sure bad URLs are invalid
        assertThrows(BadURLException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new URL("gemini.circumlunar.space");
            }
        });
    }
}
