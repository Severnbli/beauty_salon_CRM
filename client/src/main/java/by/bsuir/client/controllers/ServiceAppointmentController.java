package by.bsuir.client.controllers;

import by.bsuir.client.models.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

public class ServiceAppointmentController {
    @FXML
    private DatePicker datePicker;

    @FXML
    private Button getMasters;

    @FXML
    private ComboBox<String> mastersComboBox;

    @FXML
    private Button oderButton;

    @FXML
    private VBox serviceAppointmentScreen;

    @FXML
    private TableColumn<Service, String> serviceNameColumn;

    @FXML
    private TableColumn<Service, BigDecimal> servicePriceColumn;

    @FXML
    private TableColumn<Service, LocalTime> serviceTimeCostColumn;

    @FXML
    private TableView<Service> servicesTable;

    @FXML
    private ComboBox<LocalTime> timeComoBox;

    @Setter
    @Getter
    private static Stage stage;

    @FXML
    void onGetMasters(ActionEvent event) {

    }

    @FXML
    void onMastersComoBox(ActionEvent event) {

    }

    @FXML
    void onOrderButton(ActionEvent event) {

    }
}
