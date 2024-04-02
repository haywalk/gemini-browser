package gemtext;

import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * A block of preformatted text.
 * 
 * @author Hayden Walker
 * @version 2024-02-28
 */
public class PreformattedText implements Gemtext {
    /**
     * Preformatted block's content.
     */
    private Text text;
   
    /**
     * Create a new block of preformatted text.
     * 
     * @param content Preformatted text.
     */
    public PreformattedText(String content) {
        text = new Text(content);
        text.setFont(Font.font("monospace"));
    }

    /**
     * Return the preformatted text as a JavaFX Node.
     */
    @Override
    public Node render() {
        return text;
    }   
}
