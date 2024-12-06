package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.Master;
import by.bsuir.client.models.Order;
import by.bsuir.client.models.Service;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

public class ServiceAppointmentController implements Setupable {
    @FXML
    private DatePicker datePicker;

    @FXML
    private Button getMasters;

    @FXML
    private ComboBox<Master> mastersComboBox;

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

    private Service lastLoadingService;
    private LocalDate lastLoadingDate;
    private HashMap<Master, List<LocalTime>> lastLoadingMastersWithAvailableTimes = new HashMap<>();

    @Setter
    @Getter
    private static Stage stage;

    @FXML
    void onGetMasters(ActionEvent event) {
        Service selectedService = servicesTable.getSelectionModel().getSelectedItem();

        if (selectedService == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Бронирование услуг")
                    .content("Для просмотра доступных мастеров требуется выбрать услугу!")
                    .build().realise();
            return;
        }

        LocalDate selectedDate = datePicker.getValue();

        if (selectedDate == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Бронирование услуг")
                    .content("Для просмотра доступных мастеров требуется выбрать дату!")
                    .build().realise();
            return;
        }

        if (selectedDate.isBefore(LocalDate.now())) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Бронирование услуг")
                    .content("Выбранная дата не валидна. Выберите другую дату!");
            return;
        }

        Order order = Order.builder()
                .service(selectedService)
                .date(selectedDate.atStartOfDay())
                .build();

        final Gson gson = new Gson();

        Request request = Request.builder()
                .type(RequestType.GET_MASTERS_BY_SERVICE_AND_DATE)
                .data(gson.toJson(order))
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                "Бронирование услуг"
        );

        if (response == null) {
            return;
        }

        if (response.getStatus() != ResponseStatus.OK) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header("Бронирование услуг")
                    .content(response.getMessage())
                    .build().realise();
            return;
        }

        lastLoadingDate = selectedDate;
        Type mapType = new TypeToken<HashMap<Master, List<LocalTime>>>() {}.getType();
        lastLoadingMastersWithAvailableTimes = gson.fromJson(response.getData(), mapType);

        mastersComboBox.setItems(FXCollections.observableArrayList(lastLoadingMastersWithAvailableTimes.keySet()));
    }

    @FXML
    void onMastersComoBox(ActionEvent event) {
        Master selectedMaster = mastersComboBox.getSelectionModel().getSelectedItem();

        if (selectedMaster == null) {
            return;
        }

        timeComoBox.setItems(FXCollections.observableArrayList(lastLoadingMastersWithAvailableTimes.get(selectedMaster)));
    }

    @FXML
    void onOrderButton(ActionEvent event) {
        LocalDate selectedDate = datePicker.getValue();

        if (selectedDate == null || selectedDate != lastLoadingDate) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Бронирование записи")
                    .content("Дата была изменена или не выбрана. Подгрузите мастеров!")
                    .build().realise();
            return;
        }

        Service selectedService = servicesTable.getSelectionModel().getSelectedItem();

        if (selectedService == null || selectedService != lastLoadingService) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Бронирование записи")
                    .content("Услуга была изменена или не выбрана. Подгрузите мастеров!")
                    .build().realise();
            return;
        }

        Master selectedMaster = mastersComboBox.getSelectionModel().getSelectedItem();

        if (selectedMaster == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Бронирование услуг")
                    .content("Для бронирования требуется выбрать мастера!")
                    .build().realise();
            return;
        }

        LocalTime selectedTime = timeComoBox.getValue();
        if (selectedTime == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Бронирование услуг")
                    .content("Для бронирования услуг требуется выбрать время!")
                    .build().realise();
            return;
        }

        Order order = Order.builder()
                .client(ServerClient.getInstance().getUser())
                .master(selectedMaster)
                .service(selectedService)
                .date(selectedDate.atTime(selectedTime))
                .build();

        final Gson gson = new Gson();

        Request request = Request.builder()
                .type(RequestType.ADD_ORDER)
                .data(gson.toJson(order))
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                "Бронирование услуг"
        );

        if (response == null) {
            return;
        }

        if (response.getStatus() != ResponseStatus.OK) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header("Бронирование услуг")
                    .content(response.getMessage())
                    .build().realise();
            return;
        }

        AlertUtil.builder()
                .alertType(Alert.AlertType.INFORMATION)
                .header("Бронирование услуг")
                .content(response.getMessage())
                .build().realise();

        onGetMasters(event);
    }

    @Override
    public void setup() {
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        servicePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        serviceTimeCostColumn.setCellValueFactory(new PropertyValueFactory<>("timeCost"));

        loadServiceTable();
    }

    public void loadServiceTable() {
        Request request = Request.builder()
                .type(RequestType.GET_ALL_SERVICES)
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                "Бронирование услуг"
        );

        if (response == null) {
            return;
        }

        if (response.getStatus() == ResponseStatus.OK) {
            final Gson gson = new Gson();

            Type listType = new TypeToken<List<Service>>() {}.getType();
            List<Service> services = gson.fromJson(response.getData(), listType);

            servicesTable.setItems(FXCollections.observableArrayList(services));
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Бронирование услуг")
                    .content(response.getMessage())
                    .build().realise();
        }
    }
}
