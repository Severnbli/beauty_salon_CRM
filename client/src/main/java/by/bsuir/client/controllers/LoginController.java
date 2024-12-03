package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.tcp.RequestType;
import by.bsuir.tcp.ResponseStatus;
import by.bsuir.client.models.User;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.logging.Logger;

public class LoginController {
    private static final Logger log = Logger.getLogger(LoginController.class.getName());

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
    void onForgotPassword(ActionEvent event) {

    }

    @FXML
    void onRegister(ActionEvent event) {

    }

    @FXML
    void onLogin(ActionEvent event) {
        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Авторизация")
                    .content("Заполните логин и пароль!")
                    .build().realise();
            return;
        }

        User user = User.builder()
                .login(loginField.getText())
                .password(passwordField.getText())
                .build();

        Gson gson = new Gson();

        Response response;

        try {
            ServerClient.getInstance().sendRequest(new Request(RequestType.LOGIN, "", gson.toJson(user)));
            response = ServerClient.getInstance().getResponse();
        } catch (Exception e) {
            log.severe("Attempt of login failed: " + e);
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header("Авторизация")
                    .content("Ошибка: " + e + "!")
                    .build().realise();
            return;
        }

        if (response.getStatus() == ResponseStatus.OK) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header("Авторизация")
                    .content("Успех!")
                    .build().realise();
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Авторизация")
                    .header("Пользователя с введёнными данными не существует!")
                    .build().realise();
        }
    }
}
