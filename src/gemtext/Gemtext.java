package gemtext;

import javafx.scene.text.Text;

/**
 * A displayable Gemtext element.
 * 
 * @author Hayden Walker
 * @version 2024-02-14
 */
public interface Gemtext {
    /**
     * Return the Gemtext element rendered as text.
     * 
     * @return Rendered element as a Text object.
     */
    public Text render();
}