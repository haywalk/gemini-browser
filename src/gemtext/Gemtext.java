package gemtext;

import javafx.scene.Node;

/**
 * A displayable Gemtext element.
 * 
 * @author Hayden Walker
 * @version 2024-02-14
 */
public interface Gemtext {
    /**
     * Return the Gemtext element rendered as a JavaFX Node.
     * 
     * @return Rendered element as a JavaFX Node.
     */
    public Node render();
}