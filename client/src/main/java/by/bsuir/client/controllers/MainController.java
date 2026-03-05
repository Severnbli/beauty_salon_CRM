package by.bsuir.client.controllers;

import by.bsuir.client.App;
import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.MasterSchedule;
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
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController implements Setupable {
    @FXML
    private Menu adminMenu;

    @FXML
    private Menu clientMenu;

    @FXML
    private MenuItem closeOtherStagesItem;

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

    @FXML
    private MenuItem graphItem;

    @Getter
    private static final List<Stage> otherStages = new ArrayList<>();

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
                    servicesMenu.setVisible(false);
                    break;
                }
                case 555, 999: { // Admin
                    masterMenu.setVisible(false);
                    adminMenu.setVisible(true);
                    servicesMenu.setVisible(false);
                    break;
                }
                default: {
                    servicesMenu.setVisible(true);
                    masterMenu.setVisible(false);
                    adminMenu.setVisible(false);
                    break;
                }
            }
        }
    }

    @FXML
    void onCloseOtherStagesItem(ActionEvent event) {
        closeAllOtherStages();
    }

    public static void closeAllOtherStages() {
        closeAllOtherStagesExceptOne(null);
    }

    public static void closeAllOtherStagesExceptOne(Stage exceptStage) {
        for (int i = 0; i < otherStages.size(); i++) {
            Stage stage = otherStages.get(i);
            if (AccountManageController.getStage() != exceptStage) {
                AccountManageController.setStage(null);
            }

            if (ServiceAppointmentController.getStage() != exceptStage) {
                ServiceAppointmentController.setStage(null);
            }

            if (ViewOrdersController.getStage() != exceptStage) {
                ViewOrdersController.setStage(null);
            }

            if (stage != exceptStage) {
                if (stage != null && stage.isShowing()) {
                    stage.close();
                }
                otherStages.remove(i);
                i--;
            }
        }
    }

    @FXML
    void onGraphItem(ActionEvent event) throws IOException {
        if (GraphController.getStage() != null) {
            GraphController.getStage().toFront();
            return;
        }

        Stage stage = new Stage();
        otherStages.add(stage);

        stage.setTitle(App.getPrimaryStage().getTitle() + " - График");

        stage.setOnCloseRequest(closeEvent -> {
            GraphController.setStage(null);
            otherStages.remove(stage);
        });

        GraphController.setStage(stage);

        Loader.loadScene(stage, "/views/admin/diagram.fxml");
        stage.show();
    }

    @FXML
    void onConsumablesItem(ActionEvent event) throws IOException {
        if (ConsumableController.getStage() != null) {
            ConsumableController.getStage().toFront();
            return;
        }

        Stage stage = new Stage();
        otherStages.add(stage);

        stage.setTitle(App.getPrimaryStage().getTitle() + " - Настройка расходников");

        stage.setOnCloseRequest(closeEvent -> {
            ConsumableController.setStage(null);
            otherStages.remove(stage);
        });

        ConsumableController.setStage(stage);

        Loader.loadScene(stage, "/views/admin/consumables.fxml");
        stage.show();
    }

    @FXML
    void onLogOutItem(ActionEvent event) throws IOException {
        ServerClient.getInstance().closeConnection();

        closeAllOtherStages();

        AccountManageController.setStage(null);
        ConsumableController.setStage(null);
        ServiceAppointmentController.setStage(null);
        ServicesController.setStage(null);
        ViewOrdersController.setStage(null);
        AdminUsersController.setStage(null);
        MasterScheduleController.setStage(null);
        MasterServicesController.setStage(null);
        ReportsController.setStage(null);
        GraphController.setStage(null);

        ServerClient.getInstance().setUser(null);

        Stage stage = (Stage) logOutItem.getParentPopup().getOwnerWindow();
        Loader.loadScene(stage, "/views/general/login.fxml");
    }

    @FXML
    void onMasterScheduleItem(ActionEvent event) throws IOException {
        if (MasterScheduleController.getStage() != null) {
            MasterScheduleController.getStage().toFront();
            return;
        }

        Stage stage = new Stage();
        otherStages.add(stage);

        stage.setTitle(App.getPrimaryStage().getTitle() + " - Настройка расписания");

        stage.setOnCloseRequest(closeEvent -> {
            MasterScheduleController.setStage(null);
            otherStages.remove(stage);
        });

        MasterScheduleController.setStage(stage);

        Loader.loadScene(stage, "/views/master/schedule.fxml");
        stage.show();
    }

    @FXML
    void onMasterSkillButton(ActionEvent event) throws IOException {
        if (MasterServicesController.getStage() != null) {
            MasterServicesController.getStage().toFront();
            return;
        }

        Stage stage = new Stage();
        otherStages.add(stage);

        stage.setTitle(App.getPrimaryStage().getTitle() + " - Навыки мастера");

        stage.setOnCloseRequest(closeEvent -> {
            MasterServicesController.setStage(null);
            otherStages.remove(stage);
        });

        MasterServicesController.setStage(stage);

        Loader.loadScene(stage, "/views/master/services.fxml");
        stage.show();
    }

    @FXML
    void onReportsItem(ActionEvent event) throws IOException {
        if (ReportsController.getStage() != null) {
            ReportsController.getStage().toFront();
            return;
        }

        Stage stage = new Stage();
        otherStages.add(stage);

        stage.setTitle(App.getPrimaryStage().getTitle() + " - Генерация отчётов");

        stage.setOnCloseRequest(closeEvent -> {
            ReportsController.setStage(null);
            otherStages.remove(stage);
        });

        ReportsController.setStage(stage);

        Loader.loadScene(stage, "/views/admin/reports.fxml");
        stage.show();
    }

    @FXML
    void onServiceAppointmentItem(ActionEvent event) throws IOException {
        if (ServiceAppointmentController.getStage() != null) {
            ServiceAppointmentController.getStage().toFront();
            return;
        }

        Stage stage = new Stage();
        otherStages.add(stage);

        stage.setTitle(App.getPrimaryStage().getTitle() + " - Бронирование услуг");

        stage.setOnCloseRequest(closeEvent -> {
            ServiceAppointmentController.setStage(null);
            otherStages.remove(stage);
        });

        ServiceAppointmentController.setStage(stage);

        Loader.loadScene(stage, "/views/general/service_appointment.fxml");
        stage.show();
    }

    @FXML
    void onServicesItem(ActionEvent event) throws IOException {
        if (ServicesController.getStage() != null) {
            ServicesController.getStage().toFront();
            return;
        }

        Stage stage = new Stage();
        otherStages.add(stage);

        stage.setTitle(App.getPrimaryStage().getTitle() + " - Настройка услуг");

        stage.setOnCloseRequest(closeEvent -> {
            ServicesController.setStage(null);
            otherStages.remove(stage);
        });

        ServicesController.setStage(stage);

        Loader.loadScene(stage, "/views/admin/services.fxml");
        stage.show();
    }

    @FXML
    void onUsersItem(ActionEvent event) throws IOException {
        if (AdminUsersController.getStage() != null) {
            AdminUsersController.getStage().toFront();
            return;
        }

        Stage stage = new Stage();
        otherStages.add(stage);

        stage.setTitle(App.getPrimaryStage().getTitle() + " - Настройка профиля");

        stage.setOnCloseRequest(closeEvent -> {
            AdminUsersController.setStage(null);
            otherStages.remove(stage);
        });

        AdminUsersController.setStage(stage);

        Loader.loadScene(stage, "/views/admin/users.fxml");
        stage.show();
    }

    @FXML
    void onUserAccountItem(ActionEvent event) throws IOException {
        if (AccountManageController.getStage() != null) {
            AccountManageController.getStage().toFront();
            return;
        }

        Stage stage = new Stage();
        otherStages.add(stage);

        stage.setTitle(App.getPrimaryStage().getTitle() + " - Настройка профиля");

        stage.setOnCloseRequest(closeEvent -> {
            AccountManageController.setStage(null);
            otherStages.remove(stage);
        });

        AccountManageController.setStage(stage);

        Loader.loadScene(stage, "/views/general/account_manage.fxml");
        stage.show();
    }

    @FXML
    void onViewOrdersButton(ActionEvent event) throws IOException {
        if (ViewOrdersController.getStage() != null) {
            ViewOrdersController.getStage().toFront();
            return;
        }

        Stage stage = new Stage();
        otherStages.add(stage);

        stage.setTitle(App.getPrimaryStage().getTitle() + " - Просмотр записей");

        stage.setOnCloseRequest(closeEvent -> {
            ViewOrdersController.setStage(null);
            otherStages.remove(stage);
        });

        ViewOrdersController.setStage(stage);

        Loader.loadScene(stage, "/views/general/view_orders.fxml");
        stage.show();
    }

    @FXML
    void onViewAdminOrders(ActionEvent event) throws IOException {
        if (AdminViewOrdersController.getStage() != null) {
            AdminViewOrdersController.getStage().toFront();
            return;
        }

        Stage stage = new Stage();
        otherStages.add(stage);

        stage.setTitle(App.getPrimaryStage().getTitle() + " - Просмотр записей");

        stage.setOnCloseRequest(closeEvent -> {
            AdminViewOrdersController.setStage(null);
            otherStages.remove(stage);
        });

        AdminViewOrdersController.setStage(stage);

        Loader.loadScene(stage, "/views/admin/view_orders.fxml");
        stage.show();
    }

    @FXML
    void onViewMasterOrders(ActionEvent event) throws IOException {
        if (MasterViewOrdersController.getStage() != null) {
            MasterViewOrdersController.getStage().toFront();
            return;
        }

        Stage stage = new Stage();
        otherStages.add(stage);

        stage.setTitle(App.getPrimaryStage().getTitle() + " - Просмотр записей");

        stage.setOnCloseRequest(closeEvent -> {
            MasterViewOrdersController.setStage(null);
            otherStages.remove(stage);
        });

        MasterViewOrdersController.setStage(stage);

        Loader.loadScene(stage, "/views/master/view_orders.fxml");
        stage.show();
    }
}
