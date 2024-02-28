package browser;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import network.BadURLException;
import network.GeminiRequest;
import network.RequestFailedException;
import network.URL;

/**
 * The browser's GUI.
 * 
 * @author Hayden Walker
 * @version 2024-02-14
 */
public class Browser extends Application {
    /**
     * Default window width.
     */
    private static final int DEFAULT_WIDTH = 640;

    /**
     * Default window height.
     */
    private static final int DEFAULT_HEIGHT = 480;

    /**
     * Startup status message.
     */
    private static final String STARTUP_STATUS = "Hayden's Gemini Browser";

    /**
     * Address bar.
     */
    private TextField addressBar;

    /**
     * Status bar.
     */
    private Label statusBar;

    private Stage primaryStage;

    private String response;

    /**
     * Start the application.
     */
    @Override
    public void start(Stage primaryStage) {
        
        this.primaryStage = primaryStage;

        // create address bar
        addressBar = new TextField();

        Button submitButton = new Button();
        submitButton.setText("Submit");
        submitButton.setMaxWidth(Double.MAX_VALUE);

        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                makeRequest(addressBar.getText());
            }            
        });

        // create the top bar
        GridPane topBar = new GridPane();
        
        // set column sizes
        ColumnConstraints addressColumn = new ColumnConstraints();
        addressColumn.setPercentWidth(80);
        ColumnConstraints submitColumn = new ColumnConstraints();
        submitColumn.setPercentWidth(20);
        topBar.getColumnConstraints().addAll(addressColumn, submitColumn);
        
        // add address bar and submit button to top bar
        topBar.add(addressBar, 0, 0);
        topBar.add(submitButton, 1, 0);
        
        // create status bar
        statusBar = new Label(STARTUP_STATUS);

        // top bar to a vbox
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setBottom(statusBar);

        // add the vbox to a scene
        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        // display the window
        primaryStage.setTitle("Gemini Browser");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Given a URL as a String, make a Gemini request.
     * 
     * @param url
     */
    private void makeRequest(String url) {
        try {
            // attempt to make request
            GeminiRequest req = new GeminiRequest(new URL(url));

            // set address bar to url requested and status bar to status returned
            addressBar.setText(url);
            statusBar.setText(req.getStatus() + " " + req.getHeaderInfo());

            // process the completed request
            processRequest(req);            
        } 
        
        // handle bad URL
        catch(BadURLException e) {
            statusBar.setText("Bad Gemini URL.");
        } 
        
        // handle request failure
        catch(RequestFailedException e) {
            statusBar.setText("Request failed.");
        }
    }

    /**
     * Process a completed request.
     * 
     * @param req Request to process.
     */
    private void processRequest(GeminiRequest req) {
        // take specific actions based on the status code
        switch(req.getStatus()) {
            // input (note 11 is "sensitive" input)
            case 10: case 11:
                promptUser(req.getHeaderInfo()); // prompt user for input
                makeRequest(addressBar.getText() + "?" + response); // make a new request
                break;
            
            // success
            case 20:
                break;
            
            // redirect
            case 30: case 31:
                makeRequest(req.getHeaderInfo());
                break;

            // server unavailable
            case 40: case 41: case 42: case 43: case 44:
                break;

            // not found/bad request
            case 51: case 52: case 53: case 54: case 55:
                break;

            // certificate issue
            case 60: case 61: case 62:
                break;

            // any other status
            default:
                break;
        }
    }

    /**
     * Create a popup prompt and store the user's input in response.
     * 
     * @param prompt Prompt for the user.
     */
    private void promptUser(String prompt) {
        // create a new popup window that appears over the main window
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(primaryStage);

        // create a prompt form
        Label promptLabel = new Label(prompt);
        TextField responseField = new TextField();
        Button responseButton = new Button("Submit");
        VBox promptWindow = new VBox();
        promptWindow.getChildren().addAll(promptLabel, responseField, responseButton);

        // button sets response if one has been entered
        responseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                    if(responseField.getText().length() > 0) {
                    response = responseField.getText();
                    popup.close();
                }
            }            
        }); 

        // prevent window from closing until response is entered
        popup.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if(responseField.getText().length() == 0) {
                    event.consume();
                }            
            }
        });

        // show the window and stop executing until it has closed
        Scene popupScene = new Scene(promptWindow);
        popup.setScene(popupScene);
        popup.showAndWait();
    }

    /**
     * Start the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
