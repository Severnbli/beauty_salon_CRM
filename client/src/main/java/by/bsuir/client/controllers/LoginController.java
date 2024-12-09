package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.utils.AlertUtil;
import by.bsuir.client.utils.Loader;
import by.bsuir.tcp.RequestType;
import by.bsuir.tcp.ResponseStatus;
import by.bsuir.client.models.User;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class LoginController {
    private static final Logger log = Logger.getLogger(LoginController.class.getName());
    private static final String STAGE_NAME = "Авторизация";

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private Button loginButton;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button regButton;

    @FXML
    void onForgotPassword(ActionEvent event) throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Loader.loadScene(stage, "/views/general/reset_password.fxml");
    }

    @FXML
    void onRegister(ActionEvent event) throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Loader.loadScene(stage, "/views/general/register.fxml");
    }

    @FXML
    void onLogin(ActionEvent event) throws IOException {
        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Заполните логин и пароль!")
                    .build().realise();
            return;
        }

        ServerClient.getInstance().makeConnection();

        User user = User.builder()
                .login(loginField.getText())
                .password(passwordField.getText())
                .build();

        Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                Request.builder()
                        .type(RequestType.LOGIN)
                        .data(gson.toJson(user))
                        .build(),
                STAGE_NAME);

        if (response == null) {
            return;
        }

        if (response.getStatus() != ResponseStatus.OK) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Пользователя с введёнными данными не существует!")
                    .build().realise();

            ServerClient.getInstance().closeConnection();

            return;
        }

        User userFromResponse = gson.fromJson(response.getData(), User.class);

        if (userFromResponse.getIsDoubleEntry()) {
            ConfirmEmailController.makeSecretCode(userFromResponse.getPersonData().getEmail());

            if (!ConfirmEmailController.getConfirmed(userFromResponse.getPersonData().getEmail())) {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.ERROR)
                        .header(STAGE_NAME)
                        .content("Двойная аутентификация провалена!")
                        .build().realise();

                ServerClient.getInstance().closeConnection();

                return;
            }
        }

        ServerClient.getInstance().setUser(userFromResponse);

        Stage stage = (Stage) loginButton.getScene().getWindow();

        Loader.loadScene(stage, "/views/general/main.fxml");
    }
}
