package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.SecretCode;
import by.bsuir.client.utils.AlertUtil;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.RequestType;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class ConfirmEmailController {
    private static final String STAGE_NAME = "Подтверждение кода";

    @Getter
    private boolean confirmed = false;

    @Setter
    @Getter
    private String email;

    @FXML
    private TextField codeField;

    @FXML
    private void onConfirm() {
        if (codeField.getText().isEmpty()) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Нельзя оставлять код пустым!")
                    .build().realise();
            return;
        }

        SecretCode secretCode = SecretCode.builder()
                .email(email)
                .secretCode(codeField.getText())
                .build();

        Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        Request request = Request.builder()
                .type(RequestType.GET_IS_SECRET_CODE_VALID)
                .data(gson.toJson(secretCode))
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        if (response == null) {
            return;
        }

        if (response.getStatus() != ResponseStatus.OK) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();
            return;
        }

        confirmed = true;
        closeWindow();
    }

    @FXML
    private void onCancel() {
        confirmed = false;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) codeField.getScene().getWindow();
        stage.close();
    }

    public static boolean getConfirmed(String email) {
        try {
            FXMLLoader loader = new FXMLLoader(ConfirmEmailController.class.getResource("/views/general/get_confirmed.fxml"));
            Parent root = loader.load();

            ConfirmEmailController controller = loader.getController();
            controller.setEmail(email);

            Stage stage = new Stage();
            stage.setTitle("Подтверждение почты");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            return controller.isConfirmed();
        } catch (IOException e) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header(STAGE_NAME)
                    .content("Не удалось открыть окно подтверждения!")
                    .build().realise();
            return false;
        }
    }

    public static void makeSecretCode(String email) {
        SecretCode secretCode = SecretCode.builder()
                .email(email)
                .build();

        Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        if (email == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Нельзя создать секретный код без почты!")
                    .build();
            return;
        }

        Request request = Request.builder()
                .type(RequestType.MAKE_SECRET_CODE)
                .data(gson.toJson(secretCode))
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        if (response == null) {
            return;
        }

        if (response.getStatus() != ResponseStatus.OK) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();
        }

        AlertUtil.builder()
                .alertType(Alert.AlertType.INFORMATION)
                .header(STAGE_NAME)
                .content(response.getMessage())
                .build().realise();
    }
}

