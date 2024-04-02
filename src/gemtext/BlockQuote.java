package gemtext;

import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;

/**
 * A block quote.
 * 
 * @author Hayden Walker
 * @version 2024-02-28
 */
public class BlockQuote implements Gemtext {
    /**
     * Block quote content.
     */
    private Text text;

    /**
     * Create a new block quote.
     * 
     * @param content Quote content.
     */
    public BlockQuote(String content) {
        text = new Text(content);
        text.setFont(Font.font("Liberation Serif", FontPosture.ITALIC, 14));
    }

    /**
     * Render the quote as a JavaFX node.
     */
    @Override
    public Node render() {
        return text;
    }
    
}
