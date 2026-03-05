package by.bsuir.client;

import by.bsuir.client.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

import java.util.logging.Logger;

public class App extends Application {
    private static final Logger log = Logger.getLogger(App.class.getName());
    private static final String TITLE_NAME = "CRM - Салон красоты";

    @Getter
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/views/general/login.fxml")).load()));
        stage.setTitle(TITLE_NAME);
        stage.setResizable(false);
        stage.show();

        primaryStage = stage;

        stage.setOnCloseRequest(closeEvent -> {
            MainController.closeAllOtherStages();
        });
    }
}
