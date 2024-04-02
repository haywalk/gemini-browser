package gemtext;

import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * A paragraph.
 * 
 * @author Hayden Walker
 * @version 2024-02-28
 */
public class Paragraph implements Gemtext {
    /**
     * Paragraph text.
     */
    private Text text;
    
    /**
     * Create a new Paragraph.
     * 
     * @param content Paragraph content.
     */
    public Paragraph(String content) {
        text = new Text(content);
        text.setFont(Font.font("serif", 14));
        text.setWrappingWidth(600);
    }

    /**
     * Render the paragraph as a JavaFX Node.
     */
    @Override
    public Node render() {
        return text;
    }
}
