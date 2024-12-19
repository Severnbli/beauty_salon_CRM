package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.PersonData;
import by.bsuir.client.models.Role;
import by.bsuir.client.models.User;
import by.bsuir.client.utils.AlertUtil;
import by.bsuir.client.utils.UserValidator;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.RequestType;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminUsersController implements Initializable {
    private static final String STAGE_NAME = "Настройка пользователей";

    @Getter
    @Setter
    private static Stage stage;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TextField firstNameField;

    @FXML
    private TableColumn<User, String> lastNameColumn;

    @FXML
    private TextField lastNameField;

    @FXML
    private TableColumn<User, String> loginColumn;

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    private TableColumn<User, Role> roleColumn;

    @FXML
    private ComboBox<Role> roleComoBox;

    @FXML
    private TableView<User> usersTable;

    @FXML
    void onCreateButton(ActionEvent event) {
        if (roleComoBox.getValue() != null && roleComoBox.getValue().getAccessLevel() >= ServerClient.getInstance().getUser().getRole().getAccessLevel()) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Нельзя создавать пользователей с таким уровнем доступа!")
                    .build().realise();
            return;
        }

        PersonData personData = PersonData.builder()
                .firstName(firstNameField.getText())
                .lastName(lastNameField.getText())
                .build();

        User user = User.builder()
                .login(loginField.getText())
                .password(passwordField.getText())
                .role(roleComoBox.getValue())
                .personData(personData)
                .isDoubleEntry(false)
                .build();

        if (!UserValidator.isValidUserDataForRegister(user)) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Пользователь не валиден. Заполните все поля!")
                    .build().realise();
            return;
        }

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                Request.builder()
                        .type(RequestType.REGISTER)
                        .data(new Gson().toJson(user))
                        .build(),
                        STAGE_NAME
        );

        if (response == null) {
            return;
        }

        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();
            return;
        }

        loadTable();
        clear();
    }

    private void clear() {
        loginField.setText("");
        passwordField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        roleComoBox.setValue(null);
    }

    @FXML
    void onDeleteButton(ActionEvent event) {
        User user = getUserFromTableIsValid();

        if (user == null) {
            return;
        }

        ButtonType confirmation = AlertUtil.builder()
                .alertType(Alert.AlertType.CONFIRMATION)
                .header(STAGE_NAME)
                .content("Точно удаляем " + user.getPersonData().getFirstName() + " ?")
                .build().realiseWithConfirmation();

        if (confirmation != ButtonType.OK) {
            return;
        }

        Request request = Request.builder()
                .type(RequestType.DELETE_PROFILE)
                .data(new Gson().toJson(user))
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        if (response != null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();
        }

        loadTable();
    }

    @FXML
    void onUpdateButton(ActionEvent event) {
        User user = getUserFromTableIsValid();

        if (user == null) {
            return;
        }

        User cloneUser = user.clone();

        if (!loginField.getText().isEmpty()) {
            user.setLogin(loginField.getText());
        }

        if (!passwordField.getText().isEmpty()) {
            user.setPassword(passwordField.getText());
        }

        if (!firstNameField.getText().isEmpty()) {
            user.getPersonData().setFirstName(firstNameField.getText());
        }

        if (!lastNameField.getText().isEmpty()) {
            user.getPersonData().setLastName(lastNameField.getText());
        }

        if (roleComoBox.getValue() != null) {
            if (roleComoBox.getValue().getAccessLevel() >= ServerClient.getInstance().getUser().getRole().getAccessLevel()) {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.WARNING)
                        .header(STAGE_NAME)
                        .content("Вы не можете выдать роль равную или больше, чем у вас!")
                        .build().realise();
                return;
            } else {
                user.setRole(roleComoBox.getValue());
            }
        }

        if (!UserValidator.isValidUserDataForRegister(user)) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Пользователь не валиден!")
                    .build().realise();
            return;
        }

        if (user.equals(cloneUser)) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Вы ничего не изменили!")
                    .build().realise();
            return;
        }

        Request request = Request.builder()
                .type(RequestType.UPDATE_PROFILE)
                .data(new Gson().toJson(user))
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        if (response != null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();
        }

        loadTable();
        clear();
    }

    private User getUserFromTableIsValid() {
        User user = usersTable.getSelectionModel().getSelectedItem();

        if (user == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Пользователь не выбран")
                    .build().realise();
            return null;
        } else {
            if (user.getRole().getAccessLevel() >= ServerClient.getInstance().getUser().getRole().getAccessLevel()) {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.WARNING)
                        .header(STAGE_NAME)
                        .content("Нельзя управлять этим пользователем!")
                        .build().realise();
                return null;
            }
        }

        return user;
    }

    @FXML
    void onUpdateTableButton(ActionEvent event) {
        loadTable();
        loadComoBox();
        clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getEmail()));
        firstNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getFirstName()));
        lastNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getLastName()));
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        loadTable();
        loadComoBox();
    }

    public void loadComoBox() {
        Request request = Request.builder()
                .type(RequestType.GET_ALL_ROLES)
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

        Type listType = new TypeToken<List<Role>>() {}.getType();
        List<Role> roles = new Gson().fromJson(response.getData(), listType);

        roleComoBox.setItems(FXCollections.observableArrayList(roles));
    }

    public void loadTable() {
        Request request = Request.builder()
                .type(RequestType.GET_ALL_USERS)
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

        Type listType = new TypeToken<List<User>>() {}.getType();
        List<User> users = new Gson().fromJson(response.getData(), listType);

        usersTable.getItems().clear();
        usersTable.setItems(FXCollections.observableArrayList(users));
    }
}
