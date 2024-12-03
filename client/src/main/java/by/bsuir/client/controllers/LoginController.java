package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.enums.RequestType;
import by.bsuir.enums.ResponseStatus;
import by.bsuir.client.models.User;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.logging.Logger;

public class LoginController {
    private static final Logger log = Logger.getLogger(LoginController.class.getName());

    @FXML
    private TextField passwordField;

    @FXML
    private Button btnLogin;

    @FXML
    private Text errorText;

    @FXML
    private Hyperlink forgetPasswordLink;

    @FXML
    private TextField loginField;

    @FXML
    private Hyperlink registerLink;

    @FXML
    void onForgetPassword(ActionEvent event) {

    }

    @FXML
    void onLogin(ActionEvent event) {
        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            errorText.setVisible(true);
            errorText.setText("Please enter your login and password!");
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
        } catch (IOException | ClassNotFoundException e) {
            log.severe("Attempt of login failed: " + e);
            errorText.setVisible(true);
            errorText.setText("Login failed: something went wrong with server!");
            return;
        }

        if (response.getStatus() == ResponseStatus.OK) {
            errorText.setVisible(false);
            errorText.setText("Login successful: " + gson.fromJson(response.getData(), User.class));
        } else {
            errorText.setVisible(true);
            errorText.setText("Login failed: " + response.getMessage());
        }
    }

    @FXML
    void onRegister(ActionEvent event) {

    }
}
