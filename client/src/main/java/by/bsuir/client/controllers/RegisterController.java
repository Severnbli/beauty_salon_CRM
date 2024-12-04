package by.bsuir.client.controllers;

import by.bsuir.client.models.PersonData;
import by.bsuir.client.utils.AlertUtil;
import by.bsuir.client.utils.EmailValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private Button backToLoginButton;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

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
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/views/login.fxml")).load()));
    }

    @FXML
    void onRegister(ActionEvent event) {
        if (loginField.getText().isEmpty()
                || passwordField.getText().isEmpty()
                || firstNameField.getText().isEmpty()
                || confirmPasswordField.getText().isEmpty()) {
            AlertUtil.builder()
                    .header("Регистрация")
                    .content("Заполните все поля со звёздочками!")
                    .build().realise();
            return;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            AlertUtil.builder()
                    .header("Регистрация")
                    .content("Пароли не совпадают!")
                    .build().realise();
            return;
        }

        if (!emailField.getText().isEmpty() && !EmailValidator.isValid(emailField.getText())) {
            AlertUtil.builder()
                    .header("Регистрация")
                    .content("Проверьте правильность email-адреса!")
                    .build().realise();
            return;
        }

        PersonData personData = PersonData.builder()
                .firstName(firstNameField.getText())
                .lastName(lastNameField.getText())
                .email(emailField.getText())
                .build();

        
    }

}
