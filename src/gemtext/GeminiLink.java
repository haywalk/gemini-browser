package gemtext;

import javafx.scene.Node;
import javafx.scene.control.Hyperlink;

public class GeminiLink implements Gemtext {

    private String url;
    private Hyperlink link;

    public GeminiLink(String caption, String url) {
        this.url = url;

        link = new Hyperlink(caption);
    }

    public GeminiLink(String url) {
        this(url, url);
    }

    public String getURL() {
        return url;
    }

    @Override
    public Node render() {
        return link;
    }
    
}
