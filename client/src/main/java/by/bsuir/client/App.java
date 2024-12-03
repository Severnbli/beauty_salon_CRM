package by.bsuir.client;

import by.bsuir.client.connection.ServerClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class App extends Application {
    private static final Logger log = Logger.getLogger(App.class.getName());
    private static final String TITLE_NAME = "CRM - Салон красоты";

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/views/login.fxml")).load()));
        stage.setTitle(TITLE_NAME);
        stage.setResizable(false);
        stage.show();

        log.info("Server info: " + ServerClient.getInstance()); // Server initialization
    }
}
