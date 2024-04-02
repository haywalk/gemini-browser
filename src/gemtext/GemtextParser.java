package gemtext;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses Gemtext.
 * 
 * @author Hayden Walker
 * @version 2024-02-28
 */
public class GemtextParser {
    /**
     * Parsed Gemtext objects.
     */
    private List<Gemtext> parsedContent;

    /**
     * Create a new GemtextParser.
     * 
     * @param content Gemtext content to parse.
     */
    public GemtextParser(Byte[] content) {
        // initialize list
        parsedContent = new ArrayList<Gemtext>();

        // split content into lines
        String[] lines = makePlaintext(content).split("\n");

        // store preformatted content
        boolean preformatted = false;
        StringBuilder preformattedBuffer = new StringBuilder();

        // parse each line
        for(String line : lines) {
            // start preformatted content
            if(line.startsWith("```") && !preformatted) {
                preformatted = true;
                continue;
            }

            // end preformatted content
            else if(line.startsWith("```")) {
                preformatted = false;
                parsedContent.add(new PreformattedText(preformattedBuffer.toString()));
                preformattedBuffer = new StringBuilder(); // clear buffer       
                continue; // move onto next line         
            }

            // preformatted lines get appended to buffer
            if(preformatted) {
                preformattedBuffer.append(line);
                preformattedBuffer.append("\n");
                continue;
            }

            // append blank line
            if(line.equals("")) {
                parsedContent.add(new BlankLine());
                continue;
            }

            // parse based on first character
            switch(line.charAt(0)) {
                // parse a heading
                case '#':
                    parsedContent.add(parseHeading(line));
                    break;

                // parse a link
                case '=':
                    parsedContent.add(parseLink(line));
                    break;
                
                // parse a list
                case '*':
                    parsedContent.add(new ListItem(line.substring(2)));
                    break;

                // parse a block quote
                case '>':
                    parsedContent.add(new BlockQuote(line.substring(1)));
                    break;
                                
                // paragraph text
                default:
                    parsedContent.add(new Paragraph(line));
                    break;

            } 
        }
    }

    /**
     * Return parsed Gemtext.
     * 
     * @return Parsed Gemtext.
     */
    public List<Gemtext> getParsedContent() {
        return parsedContent;
    }

    /**
     * Parse a hyperlink.
     * 
     * @param string Line to parse.
     * @return Hyperlink.
     */
    private Gemtext parseLink(String string) {
        
        StringBuilder url = new StringBuilder();
        StringBuilder caption = new StringBuilder();
        int index = 2; // start after =>

        // advance to next non-space
        while(index < string.length() && string.charAt(index) == ' ') {
            index++;
        }

        // read URL
        while(index < string.length() && string.charAt(index) != ' ') {
            url.append(string.charAt(index++));
        }

        // advance to next non-space
        while(index < string.length() && string.charAt(index) == ' ') {
            index++;
        }

        if(index >= string.length()) {
            return new GeminiLink(url.toString());
        }

        while(index < string.length()) {
            caption.append(string.charAt(index++));
        }

        return new GeminiLink(caption.toString(), url.toString());
    }

    /**
     * Parse a heading.
     * 
     * @param string Heading to parse.
     * @return Heading object.
     */
    private Gemtext parseHeading(String string) {
        // calculate heading level (number of # characters)
        int headingLevel = 0;
        int index = 0;
        while(index < string.length() && string.charAt(index) != ' ') {
            headingLevel++;
            index++;
        }

        // create and return headings
        switch(headingLevel) {
            // heading 1
            case 1:
                return new Heading1(string.substring(2));
            
            // heading 2
            case 2:
                return new Heading2(string.substring(3));

            // heading 3 (and above)
            default:
                return new Heading3(string.substring(4));
        }      
    }



    /**
     * Convert the content to plaintext.
     * 
     * @param content Content.
     * @return Content as a String.
     */
    private String makePlaintext(Byte[] content) {
        StringBuilder sb = new StringBuilder();

        for(Byte b : content) {
            sb.append((char) b.intValue());
        }

        return sb.toString();
    }

}
