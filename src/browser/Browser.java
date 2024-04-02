package browser;

import java.util.ArrayDeque;
import java.util.List;

import gemtext.GeminiLink;
import gemtext.Gemtext;
import gemtext.GemtextParser;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
     * Startup page
     */
    private static final String STARTUP_ADDRESS = "gemini://gemini.haywalk.ca/browser.gmi";

    /**
     * Address bar.
     */
    private TextField addressBar;

    /**
     * Status bar.
     */
    private Label statusBar;

    /**
     * Content pane.
     */
    private ScrollPane contentPane;

    /**
     * Main window.
     */
    private Stage primaryStage;

    /**
     * Store user's response to prompts
     */
    private String response;

    private ArrayDeque<String> history;

    /**
     * Start the application.
     */
    @Override
    public void start(Stage primaryStage) {
        

        this.primaryStage = primaryStage;

        // create address bar
        addressBar = new TextField();

        // create submit button
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

        // create back button
        Button backButton = new Button();
        backButton.setText("Back");
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                back();
            }
        });

        Button parentFolderButton = new Button();
        parentFolderButton.setText("Parent Folder");
        parentFolderButton.setMaxWidth(Double.MAX_VALUE);
        parentFolderButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                makeRequest(new URL(addressBar.getText()).getFolderURL());
            }
        });

        // button vbox
        HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll(backButton, parentFolderButton);

        VBox menu = new VBox();
        menu.getChildren().addAll(topBar, buttonBar);

        // create status bar
        statusBar = new Label(STARTUP_STATUS);

        // create content pane
        contentPane = new ScrollPane();

        // top bar to a vbox
        BorderPane root = new BorderPane();
        root.setTop(menu);
        root.setBottom(statusBar);
        root.setCenter(contentPane);

        // add the vbox to a scene
        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        // display the window
        primaryStage.setTitle("Gemini Browser");
        primaryStage.setScene(scene);
        primaryStage.show();

        history = new ArrayDeque<String>();
        makeRequest(STARTUP_ADDRESS);

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
            history.push(url); 
            contentPane.setVvalue(contentPane.getVmin()); // resets scroll bar
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
                processContent(req);
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
     * Process content returned by the server.
     */
    private void processContent(GeminiRequest req) {
        
        String header = req.getHeaderInfo();

        // parse gemtext
        if(header.contains("text/gemini")) {
            displayGemtext(req.getContent());
        }

        // show plaintext
        else if(header.contains("text/plain")) {
            displayPlaintext(req.getContent());
        }
        // TODO make the last resort downloading binary file :)
        else {
            Downloader.download(req);
        }
    }

    /**
     * Parse and display Gemtext.
     * 
     * @param content Gemtext content as Bytes.
     */
    private void displayGemtext(Byte[] content) {
        // parse gemtext
        GemtextParser parser = new GemtextParser(content);
        List<Gemtext> gemtext = parser.getParsedContent();

        VBox contentBox = new VBox();

        for(Gemtext element : gemtext) {
            // set up links
            if(element instanceof GeminiLink) {
                GeminiLink link = (GeminiLink) element;
                Hyperlink hyperlink = (Hyperlink) link.render();
                
                hyperlink.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent arg0) {
                        // valid (complete) url
                        if(URL.isValidURL(link.getURL())) {
                            makeRequest(link.getURL());
                            return;
                        }

                        // otherwise try local url
                        String localURL = new URL(addressBar.getText()).getFolderURL();
                        if(!localURL.endsWith("/")) {
                            localURL += "/";
                        }

                        // remove / or ./
                        if(link.getURL().startsWith("/")) {
                            localURL += link.getURL().substring(1);
                        } else if(link.getURL().startsWith("./")) {
                            localURL += link.getURL().substring(2);
                        }

                        // last try to check validity
                        if(URL.isValidURL(localURL)) {
                            makeRequest(localURL);
                            return;
                        }

                        System.out.println(localURL);
                        statusBar.setText("Not a Gemini link.");
                        return;
                    }            
                });            
            }

            contentBox.getChildren().add(element.render());
        }

        contentPane.setContent(contentBox);
    }

    /**
     * Display plaintext content.
     * 
     * @param content Content to display.
     */
    private void displayPlaintext(Byte[] content) {
        StringBuilder sb = new StringBuilder();

        for(Byte b : content) {
            sb.append((char) b.intValue());
        }

        Text text = new Text(sb.toString());
        text.setFont(Font.font("monospace"));
        contentPane.setContent(text);
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

    private void back() {
        // do nothing if history is empty
        if(history.size() < 2) {
            return;
        }

        history.pop();
        String lastURL = history.pop();
        makeRequest(lastURL);
    }

    /**
     * Start the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
