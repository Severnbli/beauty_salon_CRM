package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.PersonData;
import by.bsuir.client.models.Role;
import by.bsuir.client.models.User;
import by.bsuir.client.utils.AlertUtil;
import by.bsuir.client.utils.EmailValidator;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class RegisterController {
    private static final Logger log = Logger.getLogger(RegisterController.class.getName());

    @FXML
    private Button backToLoginButton;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;

    @FXML
    void onBackToLogin(ActionEvent event) throws IOException {
        Stage stage = (Stage) backToLoginButton.getScene().getWindow();
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/views/general/login.fxml")).load()));
    }

    @FXML
    void onRegister(ActionEvent event) throws IOException {
        if (loginField.getText().isEmpty()
                || passwordField.getText().isEmpty()
                || firstNameField.getText().isEmpty()
                || confirmPasswordField.getText().isEmpty()) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Регистрация")
                    .content("Заполните все поля со звёздочками!")
                    .build().realise();
            return;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Регистрация")
                    .content("Пароли не совпадают!")
                    .build().realise();
            return;
        }

        ServerClient.getInstance().makeConnection();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                Request.builder()
                        .type(RequestType.ROLE_BY_ACCESS_LEVEL)
                        .data("0")
                        .build(),
                "Регистрация");

        if (response == null) {
            ServerClient.getInstance().closeConnection();

            return;
        }

        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header("Регистрация")
                    .content(response.getMessage())
                    .build().realise();

            ServerClient.getInstance().closeConnection();

            return;
        }

        final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        Role role = gson.fromJson(response.getData(), Role.class);

        PersonData personData = PersonData.builder()
                .firstName(firstNameField.getText())
                .lastName(lastNameField.getText())
                .build();

        User user = User.builder()
                .login(loginField.getText())
                .password(passwordField.getText())
                .role(role)
                .personData(personData)
                .isDoubleEntry(false)
                .build();

        response = ServerClient.getInstance().makeRequestAndGetResponse(
                Request.builder()
                        .type(RequestType.REGISTER)
                        .data(gson.toJson(user))
                        .build(),
                "Регистрация"
        );

        if (response == null) {
            ServerClient.getInstance().closeConnection();

            return;
        }

        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header("Регистрация")
                    .content(response.getMessage())
                    .build().realise();

            ServerClient.getInstance().closeConnection();
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header("Регистрация")
                    .content("Регистрация прошла успешно!")
                    .build().realise();

            ServerClient.getInstance().closeConnection();

            Stage stage = (Stage) registerButton.getScene().getWindow();

            Loader.loadScene(stage, "/views/general/login.fxml");
        }
    }

}
