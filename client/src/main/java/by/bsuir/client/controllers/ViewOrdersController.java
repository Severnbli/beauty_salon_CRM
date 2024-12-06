package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.Master;
import by.bsuir.client.models.Order;
import by.bsuir.client.models.Service;
import by.bsuir.client.models.StatusOfRecord;
import by.bsuir.client.utils.AlertUtil;
import by.bsuir.client.utils.Setupable;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.RequestType;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class ViewOrdersController implements Setupable {
    @FXML
    private Button delOrderButton;

    @FXML
    private TableColumn<Order, LocalDateTime> orderDateTimeColumn;

    @FXML
    private TableColumn<Order, Master> orderMasterColumn;

    @FXML
    private TableColumn<Order, Service> orderServiceColumn;

    @FXML
    private TableColumn<Order, StatusOfRecord> orderStatusColumn;

    @FXML
    private TableView<Order> ordersTable;

    @Setter
    @Getter
    private static Stage stage;

    @FXML
    void delOrderButton(ActionEvent event) {
        Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            AlertUtil.builder()
                    .header("Отмена записи")
                    .content("Для удаления требуется выбрать, что удалять!")
                    .build().realise();
            return;
        }

        ButtonType confirmation = AlertUtil.builder()
                .header("Отмена записи")
                .content("Вы точно хотите удалить запись " + selectedOrder.getService().getName() + " " +
                        selectedOrder.getDate() + "?")
                .build().realiseWithConfirmation();

        if (confirmation == ButtonType.OK) {
            final Gson gson = new Gson();

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
                        .header("Удаление записи")
                        .content(response.getMessage())
                        .build().realise();

                loadTable();
            } else {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.ERROR)
                        .header("Отмена записи")
                        .content(response.getMessage())
                        .build().realise();
            }
        }
    }

    @Override
    public void setup() {
        orderDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        orderMasterColumn.setCellValueFactory(new PropertyValueFactory<>("master"));
        orderServiceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("statusOfRecord"));

        loadTable();
    }

    private void loadTable() {
        final Gson gson = new Gson();

        Request request = Request.builder()
                .type(RequestType.GET_ORDERS_BY_USER_ID)
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
            ordersTable.getSortOrder().add(orderDateTimeColumn);
            ordersTable.sort();
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Просмотр записей")
                    .content(response.getMessage())
                    .build().realise();
        }
    }
}
