package by.bsuir.client.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Loader {
    public static void loadScene(Stage stage, String pathToFxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(Loader.class.getResource(pathToFxml));
        Parent root = loader.load();

        Object controller = loader.getController();

        if (controller instanceof Setupable) {
            ((Setupable) controller).setup();
        }

        stage.setScene(new Scene(root));
    }
}
