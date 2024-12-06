package by.bsuir.client.controllers;

import by.bsuir.client.models.Master;
import by.bsuir.client.models.Order;
import by.bsuir.client.models.Service;
import by.bsuir.client.models.StatusOfRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDateTime;

public class ViewOrdersController {

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

    @FXML
    void delOrderButton(ActionEvent event) {

    }
}
