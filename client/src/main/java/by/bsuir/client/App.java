package by.bsuir.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static final String TITLE_NAME = "CRM - Салон красоты";

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/views/login.fxml")).load()));
        stage.setTitle(TITLE_NAME);
        stage.setResizable(false);
        stage.show();
    }
}
