package browser;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The browser's GUI.
 * 
 * @author Hayden Walker
 * @version 2024-02-14
 */
public class Browser extends Application {
    /**
     * Start the application.
     */
    @Override
    public void start(Stage primaryStage) {
        // create a button
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        // add the button to a stackpane
        StackPane root = new StackPane();
        root.getChildren().add(btn);

        // add the button to the scene
        Scene scene = new Scene(root, 300, 250);

        // display the window
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Start the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}

