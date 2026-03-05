package by.bsuir.client.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertUtil {
    private String header;
    private String content;
    private Alert.AlertType alertType;

    public void realise() {
        complete().showAndWait();
    }

    public ButtonType realiseWithConfirmation() {
        return complete().showAndWait().orElse(ButtonType.CANCEL);
    }

    public Alert complete() {
        Alert alert = new Alert(alertType);
        alert.setTitle("CRM - Салон красоты");
        alert.setHeaderText(header);
        alert.setContentText(content);

        return alert;
    }
}
