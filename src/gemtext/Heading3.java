package gemtext;

import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * A third-level heading.
 * 
 * @author Hayden Walker
 * @version 2024-02-28
 */
public class Heading3 implements Gemtext {
    /**
     * Heading's text content.
     */
    private Text text;
    
    /**
     * Create a new Heading3 object.
     * 
     * @param content Heading content.
     */
    public Heading3(String content) {
        text = new Text(content);
        text.setFont(Font.font("serif", FontWeight.BOLD, 16));
    }

    /**
     * Return the heading as a JavaFX Node.
     */
    @Override
    public Node render() {
        return text;
    }
}
