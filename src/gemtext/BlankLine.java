package gemtext;

import javafx.scene.Node;
import javafx.scene.text.Text;

public class BlankLine implements Gemtext {

    @Override
    public Node render() {
        return new Text("\n");
    } 
    
}
