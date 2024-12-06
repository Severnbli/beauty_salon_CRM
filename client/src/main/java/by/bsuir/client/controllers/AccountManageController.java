package by.bsuir.client.controllers;

import by.bsuir.client.App;
import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.User;
import by.bsuir.client.utils.AlertUtil;
import by.bsuir.client.utils.EmailValidator;
import by.bsuir.client.utils.Loader;
import by.bsuir.client.utils.Setupable;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.RequestType;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AccountManageController implements Setupable {
    @FXML
    private VBox clientInfoScreen;

    @FXML
    private Button delAccountButton;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button updateAccountButton;

    @Override
    public void setup() {
        final User client = ServerClient.getInstance().getUser();

        if (client != null) {
            if (client.getPersonData() != null) {
                firstNameField.setText(client.getPersonData().getFirstName());
                lastNameField.setText(client.getPersonData().getLastName());
                emailField.setText(client.getPersonData().getEmail());
            } else {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.ERROR)
                        .header("Система")
                        .content("Пользовательские данные не корректны!")
                        .build().realise();
            }
            loginField.setText(client.getLogin());
            passwordField.setText("");
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header("Система")
                    .content("Пользователь не зарегистрирован. Пройдите этап авторизации.")
                    .build().realise();
        }
    }

    @FXML
    void onDelAccountButton(ActionEvent event) throws IOException {
        ButtonType confirmation = AlertUtil.builder()
                .alertType(Alert.AlertType.CONFIRMATION)
                .header("Удаление профиля")
                .content("Вы уверены?")
                .build().realiseWithConfirmation();

        if (confirmation == ButtonType.OK) {
            final Gson gson = new Gson();
            final User client = ServerClient.getInstance().getUser();

            Request request = Request.builder()
                    .type(RequestType.DELETE_PROFILE)
                    .data(gson.toJson(client))
                    .build();

            Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                    request,
                    "Удаление профиля"
            );

            if (response == null) {
                return;
            }

            if (response.getStatus() == ResponseStatus.OK) {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.INFORMATION)
                        .header("Удаление профиля")
                        .content("Удаление завершено успешно!")
                        .build().realise();

                ServerClient.getInstance().setUser(null);

                Stage currentStage = (Stage) delAccountButton.getScene().getWindow();
                MainController.closeAllOtherStagesExceptOne(currentStage);

                Loader.loadScene(App.getPrimaryStage(), "/views/general/login.fxml");

                MainController.closeAllOtherStages();
            } else {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.ERROR)
                        .header("Удаление профиля")
                        .content(response.getMessage())
                        .build().realise();
            }
        }
    }

    @FXML
    void onUpdateAccountButton(ActionEvent event) {
        if (firstNameField.getText().isEmpty()) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Настройка профиля")
                    .content("Вы не можете удалить себе имя!")
                    .build().realise();
            return;
        }

        if (loginField.getText().isEmpty()) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Настройка профиля")
                    .content("Вы не можете удалить себе логин!")
                    .build().realise();
            return;
        }

        final User client = ServerClient.getInstance().getUser();

        if (!emailField.getText().equals(client.getPersonData().getEmail()) && !emailField.getText().isEmpty()
        && !EmailValidator.isValid(emailField.getText())) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Настройка профиля")
                    .content("Электронная почта не валидна!")
                    .build().realise();
            return;
        }

        if (!firstNameField.getText().equals(client.getPersonData().getFirstName()) ||
        !lastNameField.getText().equals(client.getPersonData().getLastName()) ||
        !emailField.getText().equals(client.getPersonData().getEmail()) ||
        !loginField.getText().equals(client.getLogin()) ||
        !passwordField.getText().isEmpty()) {
            client.setLogin(loginField.getText());

            if (!passwordField.getText().isEmpty()) {
                client.setPassword(passwordField.getText());
            }

            client.getPersonData().setFirstName(firstNameField.getText());
            client.getPersonData().setLastName(lastNameField.getText());
            client.getPersonData().setEmail(emailField.getText());

            final Gson gson = new Gson();

            Request request = Request.builder()
                    .type(RequestType.UPDATE_PROFILE)
                    .data(gson.toJson(client))
                    .build();

            Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                    request,
                    "Настройка профиля"
            );

            if (response == null) {
                return;
            }

            if (response.getStatus() == ResponseStatus.ERROR) {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.ERROR)
                        .header("Настройка профиля")
                        .content(response.getMessage())
                        .build().realise();
            } else {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.INFORMATION)
                        .header("Настройка профиля")
                        .content("Настройка профиля успешна!")
                        .build().realise();

                ServerClient.getInstance().setUser(gson.fromJson(response.getData(), User.class));

                setup();
            }
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Настройка профиля")
                    .content("Вы ничего не изменили!")
                    .build().realise();
        }
    }
}
