package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.User;
import by.bsuir.client.utils.AlertUtil;
import by.bsuir.client.utils.Loader;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.RequestType;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ResetPasswordController {
    private static final String STAGE_NAME = "Восстановление доступа";

    @FXML
    private Button backToLoginButton;

    @FXML
    private TextField loginField;

    @FXML
    void onBackToLoginButton(ActionEvent event) throws IOException {
        Stage stage = (Stage) backToLoginButton.getScene().getWindow();
        Loader.loadScene(stage, "/views/general/login.fxml");
    }

    @FXML
    void onResetPasswordButton(ActionEvent event) throws IOException {
        if (loginField.getText().isEmpty()) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Требуется заполнить поле с логином!")
                    .build().realise();
            return;
        }

        User user = User.builder()
                .login(loginField.getText())
                .build();

        Request request = Request.builder()
                .type(RequestType.GET_IS_USER_EXIST_BY_LOGIN)
                .data(new Gson().toJson(user))
                .build();

        ServerClient.getInstance().makeConnection();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        ServerClient.getInstance().closeConnection();

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

        Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        User userFromResponse = gson.fromJson(response.getData(), User.class);

        ServerClient.getInstance().makeConnection();

        if (userFromResponse.getPersonData().getEmail() == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("У указанного пользователя отсутствует почта!")
                    .build().realise();
            return;
        }

        ConfirmEmailController.makeSecretCode(userFromResponse.getPersonData().getEmail());

        boolean isConfirmed = ConfirmEmailController.getConfirmed(userFromResponse.getPersonData().getEmail());

        ServerClient.getInstance().closeConnection();

        if (!isConfirmed) {
            return;
        }

        request = Request.builder()
                .type(RequestType.RESET_USER_PASSWORD_VIA_LOGIN)
                .data(gson.toJson(user))
                .build();

        ServerClient.getInstance().makeConnection();

        response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        ServerClient.getInstance().closeConnection();

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

        AlertUtil.builder()
                .alertType(Alert.AlertType.INFORMATION)
                .header(STAGE_NAME)
                .content(response.getMessage())
                .build().realise();

        Stage stage = (Stage) backToLoginButton.getScene().getWindow();
        Loader.loadScene(stage, "/views/general/login.fxml");
    }
}