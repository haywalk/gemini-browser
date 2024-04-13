package gemtext;

import javafx.scene.Node;
import javafx.scene.text.Text;

public class ListItem implements Gemtext {
    
    private Text text;

    public ListItem(String content) {
        text = new Text(content);
    }

    @Override
    public Node render() {
        return text;
    }
}
