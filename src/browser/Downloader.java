package browser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import network.GeminiRequest;

/**
 * Downloads files.
 * 
 * @author Hayden Walker
 * @version 2024-04-02
 */
public class Downloader {
    /**
     * Save content from a Gemini request.
     * 
     * @param req GeminiRequest to save content from.
     */
    public static void download(GeminiRequest req) {
        // TODO clean this up, or use it for URL?
        // get filename to save to
        String[] tokens = req.url().split("/");
        String name = tokens[tokens.length - 1];
        
        // open file to write
        File file = new File(name);
        file.delete();
        RandomAccessFile raf;

        try{
            raf = new RandomAccessFile(file, "rw");
        } catch(FileNotFoundException e) {
            return;
        }

        // convert to primitive byte array
        byte[] bytes = new byte[req.getContent().length];
        for(int i = 0; i < bytes.length; i++) {
            bytes[i] = req.getContent()[i];
        }
        
        // write bytes to file
        try{
            raf.write(bytes);
            raf.close();
        } catch(IOException e) {
            return;
        }
    }
}
