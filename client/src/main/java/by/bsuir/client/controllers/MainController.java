package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.User;
import by.bsuir.client.utils.AlertUtil;
import by.bsuir.client.utils.Loader;
import by.bsuir.client.utils.Setupable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController implements Setupable {
    @FXML
    private Menu adminMenu;

    @FXML
    private Menu clientMenu;

    @FXML
    private Menu closeOtherStages;

    @FXML
    private MenuItem consumablesItem;

    @FXML
    private MenuItem logOutItem;

    @FXML
    private VBox mainScreenWindow;

    @FXML
    private Menu masterMenu;

    @FXML
    private MenuItem masterScheduleItem;

    @FXML
    private MenuItem masterSkillItem;

    @FXML
    private MenuItem ordersItems;

    @FXML
    private MenuItem reportsItem;

    @FXML
    private MenuItem serviceAppointmentItem;

    @FXML
    private MenuItem servicesItem;

    @FXML
    private Menu servicesMenu;

    @FXML
    private MenuItem userAccountItem;

    @FXML
    private MenuItem usersItem;

    private List<Stage> otherStages = new ArrayList<>();

    @Override
    public void setup() {
        User client = ServerClient.getInstance().getUser();

        if (client == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header("Система")
                    .content("Пользователь не зарегистрирован. Пройдите этап авторизации.")
                    .build().realise();

            return;
        }

        if (client.getRole() != null) {
            switch (client.getRole().getAccessLevel()) {
                case 111: { // Master
                    masterMenu.setVisible(true);
                    adminMenu.setVisible(false);
                    break;
                }
                case 555, 999: { // Admin
                    masterMenu.setVisible(false);
                    adminMenu.setVisible(true);
                    break;
                }
                default: {
                    masterMenu.setVisible(false);
                    adminMenu.setVisible(false);
                    break;
                }
            }
        }
    }

    @FXML
    void onCloseOtherStages(ActionEvent event) {
        for (Stage stage: otherStages) {
            if (stage != null && stage.isShowing()) {
                stage.close();
            }
            otherStages.remove(stage);
        }
    }

    @FXML
    void onConsumablesItem(ActionEvent event) {

    }

    @FXML
    void onLogOutItem(ActionEvent event) throws IOException {
        onCloseOtherStages(event);

        ServerClient.getInstance().setUser(null);

        Stage stage = (Stage) logOutItem.getParentPopup().getOwnerWindow();
        Loader.loadScene(stage, "/views/general/login.fxml");
    }

    @FXML
    void onMasterScheduleItem(ActionEvent event) {

    }

    @FXML
    void onMasterSkillButton(ActionEvent event) {

    }

    @FXML
    void onReportsItem(ActionEvent event) {

    }

    @FXML
    void onServiceAppointmentItem(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        otherStages.add(stage);

        Loader.loadScene(stage, "/views/general/service_appointment.fxml");
        stage.show();
    }

    @FXML
    void onServicesItem(ActionEvent event) {

    }

    @FXML
    void onUserAccountItem(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        otherStages.add(stage);

        Loader.loadScene(stage, "/views/general/account_manage.fxml");
        stage.show();
    }

    @FXML
    void onViewOrdersButton(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        otherStages.add(stage);

        Loader.loadScene(stage, "/views/general/view_orders.fxml");
        stage.show();
    }

    @FXML
    void usersItem(ActionEvent event) {

    }
}
