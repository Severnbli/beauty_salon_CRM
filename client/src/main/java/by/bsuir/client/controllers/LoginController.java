package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.utils.AlertUtil;
import by.bsuir.tcp.RequestType;
import by.bsuir.tcp.ResponseStatus;
import by.bsuir.client.models.User;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import com.google.gson.Gson;
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
    void onRegister(ActionEvent event) throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/views/general/register.fxml")).load()));
    }

    @FXML
    void onLogin(ActionEvent event) throws IOException {
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

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                Request.builder()
                        .type(RequestType.LOGIN)
                        .data(gson.toJson(user))
                        .build(),
                "Авторизация");

        if (response == null) {
            return;
        }

        if (response.getStatus() == ResponseStatus.OK) {
            ServerClient.getInstance().setUser(gson.fromJson(response.getData(), User.class));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/views/general/main.fxml")).load()));
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Авторизация")
                    .content("Пользователя с введёнными данными не существует!")
                    .build().realise();
        }
    }
}
