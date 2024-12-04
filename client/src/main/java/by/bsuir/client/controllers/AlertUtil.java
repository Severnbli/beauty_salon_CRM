package by.bsuir.client.controllers;

import javafx.scene.control.Alert;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertUtil {
    private String header;
    private String content;
    private Alert.AlertType alertType;

    public void realise() {
        Alert alert = new Alert(alertType);
        alert.setTitle("CRM - Салон красоты");
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }
}
