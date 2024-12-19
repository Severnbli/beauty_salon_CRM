package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.Master;
import by.bsuir.client.models.Order;
import by.bsuir.client.models.Service;
import by.bsuir.client.models.StatusOfRecord;
import by.bsuir.client.utils.AlertUtil;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.RequestType;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class MasterViewOrdersController implements Initializable {
    final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

    @Setter
    @Getter
    private static Stage stage;

    @FXML
    private Button delOrderButton;

    @FXML
    private Button executedButton;

    @FXML
    private TableColumn<Order, String> orderDateTimeColumn;

    @FXML
    private TableColumn<Order, String> orderClientColumn;

    @FXML
    private TableColumn<Order, Service> orderServiceColumn;

    @FXML
    private TableColumn<Order, StatusOfRecord> orderStatusColumn;

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    void onDelOrderButton(ActionEvent event) {
        Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header("Отмена записи")
                    .content("Для отмены требуется выбрать, что отменять!")
                    .build().realise();
            return;
        }

        ButtonType confirmation = AlertUtil.builder()
                .alertType(Alert.AlertType.CONFIRMATION)
                .header("Отмена записи")
                .content("Вы точно хотите отменить запись " + selectedOrder.getService().getName() + " " +
                        selectedOrder.getDate() + " от " + selectedOrder.getClient().getPersonData().getFirstName() + "?")
                .build().realiseWithConfirmation();

        if (confirmation == ButtonType.OK) {
            final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

            Request request = Request.builder()
                    .type(RequestType.REJECT_ORDER)
                    .data(gson.toJson(selectedOrder))
                    .build();

            Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                    request,
                    "Отмена записи"
            );

            if (response == null) {
                return;
            }

            if (response.getStatus() != ResponseStatus.OK) {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.ERROR)
                        .header("Отмена записи")
                        .content(response.getMessage())
                        .build().realise();
            } else {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.INFORMATION)
                        .header("Отмена записи")
                        .content(response.getMessage())
                        .build().realise();

                loadTable();
            }
        }
    }

    @FXML
    void onExecutedButton(ActionEvent event) {
        Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header("Отмена записи")
                    .content("Для отмены требуется выбрать, что подтверждать!")
                    .build().realise();
            return;
        }

        ButtonType confirmation = AlertUtil.builder()
                .alertType(Alert.AlertType.CONFIRMATION)
                .header("Отмена записи")
                .content("Вы точно хотите подтвердить выполнение записи " + selectedOrder.getService().getName() + " " +
                        selectedOrder.getDate() + " от " + selectedOrder.getClient().getPersonData().getFirstName() + "?")
                .build().realiseWithConfirmation();

        if (confirmation == ButtonType.OK) {
            final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

            Request request = Request.builder()
                    .type(RequestType.EXECUTE_ORDER)
                    .data(gson.toJson(selectedOrder))
                    .build();

            Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                    request,
                    "Подтверждение выполнения записи"
            );

            if (response == null) {
                return;
            }

            if (response.getStatus() != ResponseStatus.OK) {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.ERROR)
                        .header("Подтверждение выполнения записи")
                        .content(response.getMessage())
                        .build().realise();
            } else {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.INFORMATION)
                        .header("Подтверждение выполнения записи")
                        .content(response.getMessage())
                        .build().realise();

                loadTable();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        orderClientColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClient().getPersonData().getFirstName()));
        orderServiceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("statusOfRecord"));
        orderDateTimeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")))
        );

        loadTable();
    }

    public void loadTable() {
        Request request = Request.builder()
                .type(RequestType.GET_ORDERS_BY_MASTER_ID)
                .data(gson.toJson(ServerClient.getInstance().getUser()))
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                "Просмотр записей"
        );

        if (response == null) {
            return;
        }

        if (response.getStatus() == ResponseStatus.OK) {
            Type listType = new TypeToken<List<Order>>() {}.getType();
            List<Order> orders = gson.fromJson(response.getData(), listType);

            ordersTable.setItems(FXCollections.observableArrayList(orders));

            orderDateTimeColumn.setComparator(Comparator.naturalOrder());

            ordersTable.getSortOrder().clear();
            ordersTable.getSortOrder().add(orderStatusColumn);
            ordersTable.sort();
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Просмотр записей")
                    .content(response.getMessage())
                    .build().realise();
        }
        System.out.println("A");
    }
}
