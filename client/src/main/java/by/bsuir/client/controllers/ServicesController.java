package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.Consumable;
import by.bsuir.client.models.Service;
import by.bsuir.client.models.ServiceConsumable;
import by.bsuir.client.utils.AlertUtil;
import by.bsuir.client.utils.Setupable;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.RequestType;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServicesController implements Setupable {
    @Getter
    @Setter
    public static Stage stage;

    public static final String STAGE_NAME = "Настройка услуг";

    private Service nowConsumableService;

    @FXML
    private Button eraseButton;

    @FXML
    private Button addConsumableButton;

    @FXML
    private Button addServiceButton;

    @FXML
    private AnchorPane awd;

    @FXML
    private TableColumn<Consumable, String> consumableNameColumn;

    @FXML
    private TableColumn<Consumable, Integer> consumableQuantityColumn;

    @FXML
    private ComboBox<Consumable> consumablesComoBox;

    @FXML
    private TableView<Consumable> consumablesTable;

    @FXML
    private Button delConsumableButton;

    @FXML
    private Button delServiceButton;

    @FXML
    private ComboBox<Integer> hourComoBox;

    @FXML
    private TextArea infoArea;

    @FXML
    private ComboBox<Integer> minutesComoBox;

    @FXML
    private TableColumn<Service, String> serviceNameColumn;

    @FXML
    private TextField serviceNameField;

    @FXML
    private TableColumn<Service, BigDecimal> servicePriceColumn;

    @FXML
    private TextField servicePriceField;

    @FXML
    private TableColumn<Service, LocalTime> serviceTimeCostColumn;

    @FXML
    private TableView<Service> servicesTable;

    @FXML
    private Button updateConsumableTableButton;

    @FXML
    private Button updateServiceButton;

    @FXML
    void onEraseButton(ActionEvent event) {
        serviceNameField.setText("");
        servicePriceField.setText("");
        hourComoBox.setValue(null);
        minutesComoBox.setValue(null);
        nowConsumableService = null;
        consumablesTable.getItems().clear();
        infoArea.setText("");
    }

    @FXML
    void onAddConsumableButton(ActionEvent event) {
        final Consumable selectedConsumable = consumablesComoBox.getValue();

        if (selectedConsumable == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Перед добавлением выберете расходник!")
                    .build().realise();
            return;
        }

        if (nowConsumableService == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Перед добавлением подгрузите расходники для конкретной услуги!")
                    .build().realise();
            return;
        }

        ServiceConsumable serviceConsumable = ServiceConsumable.builder()
                .service(nowConsumableService)
                .consumable(selectedConsumable)
                .build();

        final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        Request request = Request.builder()
                .type(RequestType.ADD_SERVICE_CONSUMABLE)
                .data(gson.toJson(serviceConsumable))
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
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();

            loadConsumableTable();
        }
    }

    @FXML
    void onAddServiceButton(ActionEvent event) {
        if (serviceNameField.getText().isEmpty() || servicePriceField.getText().isEmpty() ||
        hourComoBox.getValue() == null || minutesComoBox.getValue() == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Заполните все необходимые поля для добавления услуги!")
                    .build().realise();
            return;
        }

        BigDecimal servicePrice;

        try {
            servicePrice = new BigDecimal(servicePriceField.getText());
        } catch (NumberFormatException e) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Цена должна быть представлена числами!")
                    .build().realise();
            return;
        }

        Service service = Service.builder()
                .name(serviceNameField.getText())
                .price(servicePrice)
                .timeCost(LocalTime.of(hourComoBox.getValue(), minutesComoBox.getValue()))
                .build();

        final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        Request request = Request.builder()
                .type(RequestType.ADD_SERVICE)
                .data(gson.toJson(service))
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
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();

            loadServicesTable();
            onEraseButton(event);
        }
    }

    @FXML
    void onDelConsumableButton(ActionEvent event) {
        final Consumable selectedConsumable = consumablesComoBox.getValue();

        if (selectedConsumable == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Перед удалением выберете расходник!")
                    .build().realise();
            return;
        }

        if (nowConsumableService == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Перед удалением подгрузите расходники для конкретной услуги!")
                    .build().realise();
            return;
        }

        ServiceConsumable serviceConsumable = ServiceConsumable.builder()
                .service(nowConsumableService)
                .consumable(selectedConsumable)
                .build();

        final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        Request request = Request.builder()
                .type(RequestType.DELETE_SERVICE_CONSUMABLE)
                .data(gson.toJson(serviceConsumable))
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
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();

            loadConsumableTable();
        }
    }

    @FXML
    void onDelServiceButton(ActionEvent event) {
        Service selectedService = servicesTable.getSelectionModel().getSelectedItem();

        boolean isNowConsumableServiceIsDeleted = selectedService == nowConsumableService;

        if (selectedService == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Для удаления услуги, её нужно выбрать в таблице!")
                    .build().realise();
            return;
        }

        ButtonType confirmation = AlertUtil.builder()
                .alertType(Alert.AlertType.CONFIRMATION)
                .header(STAGE_NAME)
                .content("Вы точно хотите удалить услугу " + selectedService.getName() + "?")
                .build().realiseWithConfirmation();

        if (confirmation != ButtonType.OK) {
            return;
        }

        final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        Request request = Request.builder()
                .type(RequestType.DELETE_SERVICE)
                .data(gson.toJson(selectedService))
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
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();

            if (isNowConsumableServiceIsDeleted) {
                onEraseButton(event);
            }

            loadServicesTable();
        }
    }

    @FXML
    void onUpdateConsumableTableButton(ActionEvent event) {
        nowConsumableService = servicesTable.getSelectionModel().getSelectedItem();

        if (nowConsumableService == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Выберите услугу перед подгрузкой расходников!")
                    .build().realise();
            return;
        }

        loadConsumableTable();
    }

    @FXML
    void onUpdateServiceButton(ActionEvent event) {
        Service selectedService = servicesTable.getSelectionModel().getSelectedItem();

        if (selectedService == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Для редактирования услуги необходимо выбрать ёё в таблице!")
                    .build().realise();
            return;
        }

        if (serviceNameField.getText().isEmpty() && servicePriceField.getText().isEmpty() &&
                hourComoBox.getValue() == null && minutesComoBox.getValue() == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Изменение услуги бесполезно без изменения параметров!")
                    .build().realise();
            return;
        }

        if ((hourComoBox.getValue() != null && minutesComoBox.getValue() == null) || (hourComoBox.getValue() == null && minutesComoBox.getValue() != null)) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Длительность услуги должна быть выбрана полностью!")
                    .build().realise();
            return;
        } else {
            if (hourComoBox.getValue() != null && minutesComoBox.getValue() != null) {
                selectedService.setTimeCost(LocalTime.of(hourComoBox.getValue(), minutesComoBox.getValue()));
            }
        }


        if (!servicePriceField.getText().isEmpty()) {
            try {
                selectedService.setPrice(new BigDecimal(servicePriceField.getText()));
            } catch (NumberFormatException e) {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.WARNING)
                        .header(STAGE_NAME)
                        .content("Цена должна быть представлена числами!")
                        .build().realise();
                return;
            }
        }

        if (!serviceNameField.getText().isEmpty()) {
            selectedService.setName(serviceNameField.getText());
        }

        final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        Request request = Request.builder()
                .type(RequestType.UPDATE_SERVICE)
                .data(gson.toJson(selectedService))
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
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();

            loadServicesTable();
            onEraseButton(event);
        }
    }

    @Override
    public void setup() {
        consumableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        consumableQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        servicePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        serviceTimeCostColumn.setCellValueFactory(new PropertyValueFactory<>("timeCost"));

        List<Integer> integers = new ArrayList<>();

        int i = 0;

        for (; i <= 8; i++) {
            integers.add(i);
        }
        hourComoBox.setItems(FXCollections.observableArrayList(integers));

        for (; i <= 59; i++) {
            integers.add(i);
        }
        minutesComoBox.setItems(FXCollections.observableArrayList(integers));

        loadConsumablesComoBox();

        loadServicesTable();
    }

    public void loadConsumablesComoBox() {
        Request request = Request.builder()
                .type(RequestType.GET_ALL_CONSUMABLES)
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
        } else {
            final Gson gson = new Gson();

            Type listType = new TypeToken<List<Consumable>>() {}.getType();
            List<Consumable> consumables = gson.fromJson(response.getData(), listType);

            consumablesComoBox.setItems(FXCollections.observableArrayList(consumables));
        }
    }

    public void loadTables() {
        loadServicesTable();
        loadConsumableTable();
    }

    public void loadServicesTable() {
        Request request = Request.builder()
                .type(RequestType.GET_ALL_SERVICES)
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        if (response == null) {
            return;
        }

        if (response.getStatus() == ResponseStatus.OK) {
            final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

            Type listType = new TypeToken<List<Service>>() {}.getType();
            List<Service> services = gson.fromJson(response.getData(), listType);

            servicesTable.setItems(FXCollections.observableArrayList(services));
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();
        }
    }

    public void loadConsumableTable() {
        if (nowConsumableService == null) {
            nowConsumableService = servicesTable.getSelectionModel().getSelectedItem();

            if (nowConsumableService == null) {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.WARNING)
                        .header(STAGE_NAME)
                        .content("Выберите услугу перед подгрузкой расходников!")
                        .build().realise();
                return;
            }
        }

        final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        Request request = Request.builder()
                .type(RequestType.GET_CONSUMABLES_BY_SERVICE)
                .data(gson.toJson(nowConsumableService))
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

        Type listType = new TypeToken<List<Consumable>>() {}.getType();
        List<Consumable> consumables = gson.fromJson(response.getData(), listType);

        consumablesTable.setItems(FXCollections.observableArrayList(consumables));

        infoArea.setText("Сводка по расходникам актуальна для услуги " + nowConsumableService.getName() +
                ". Всего расходников задействовано в услуге: " + consumables.size() + ".");
    }
}
