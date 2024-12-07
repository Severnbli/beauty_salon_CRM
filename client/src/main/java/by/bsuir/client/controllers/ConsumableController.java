package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.Consumable;
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
import java.util.ArrayList;
import java.util.List;

public class ConsumableController implements Setupable {
    @Setter
    @Getter
    private static Stage stage;

    @FXML
    private TextField consumableCreationQuantity;

    @FXML
    private Button addButton;

    @FXML
    private TextField consumableCreationName;

    @FXML
    private TableColumn<Consumable, Long> consumableIdColumn;

    @FXML
    private TableColumn<Consumable, String> consumableNameColumn;

    @FXML
    private TableColumn<Consumable, Integer> consumableQuantityColumn;

    @FXML
    private TableView<Consumable> consumableTable;

    @FXML
    private Button delButton;

    @FXML
    private Button editButton;

    @FXML
    private TextField editConsumableId;

    @FXML
    private TextField editConsumableQuantity;

    @FXML
    private TextField editConsumableName;

    @FXML
    private ComboBox<String> quantityOperation;

    private static final String STAGE_NAME = "Настройка расходников";

    @FXML
    void onClearButton(ActionEvent event) {
        consumableCreationName.setText("");
        editConsumableName.setText("");
        editConsumableQuantity.setText("");
        editConsumableId.setText("");
        consumableCreationQuantity.setText("");
    }

    @FXML
    void onAddButton(ActionEvent event) {
        if (consumableCreationName.getText().isEmpty()) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Для создания задайте имя расходнику!")
                    .build().realise();
            return;
        }

        if (consumableCreationQuantity.getText().isEmpty()) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Для создания задайте количество расходника!")
                    .build().realise();
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(consumableCreationQuantity.getText());
        } catch (NumberFormatException e) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Формат числа неверен!")
                    .build().realise();
            return;
        }

        final Gson gson = new Gson();
        final Consumable consumable = Consumable.builder()
                .name(consumableCreationName.getText())
                .quantity(quantity)
                .build();

        Request request = Request.builder()
                .type(RequestType.ADD_CONSUMABLE)
                .data(gson.toJson(consumable))
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        if (response == null) {
            return;
        }

        if (response.getStatus() == ResponseStatus.OK) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();

            consumableCreationName.setText("");
            consumableCreationQuantity.setText("");

            loadTable();
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();
        }
    }

    @FXML
    void onEditButton(ActionEvent event) {
        if (editConsumableId.getText().isEmpty()) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Для редактирования выберите номер!")
                    .build().realise();
            return;
        }

        if (editConsumableName.getText().isEmpty() && editConsumableQuantity.getText().isEmpty()) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Нечего редактировать!")
                    .build().realise();
            return;
        }

        long id = 0L;
        try {
            id = Long.parseLong(editConsumableId.getText());
        } catch (NumberFormatException e) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Введён неверный номер расходника!")
                    .build().realise();
        }

        Request request = Request.builder()
                .type(RequestType.GET_CONSUMABLE_BY_ID)
                .data(Long.toString(id))
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

        final Gson gson = new Gson();

        Consumable consumable = gson.fromJson(response.getData(), Consumable.class);

        if (!editConsumableName.getText().isEmpty()) {
            consumable.setName(editConsumableName.getText());
        }

        if (!editConsumableQuantity.getText().isEmpty()) {
            int quantity;

            try {
                quantity = Integer.parseInt(editConsumableQuantity.getText());
            } catch (NumberFormatException e) {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.WARNING)
                        .header(STAGE_NAME)
                        .content("Неверный формат прибавочного числа!")
                        .build().realise();
                return;
            }

            if (quantityOperation.getValue() == null) {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.WARNING)
                        .header(STAGE_NAME)
                        .content("Выберите операцию для значения!")
                        .build().realise();
                return;
            }

            if (quantityOperation.getValue().equals("Добавление")) {
                consumable.setQuantity(quantity + consumable.getQuantity());
            } else {
                consumable.setQuantity(consumable.getQuantity() - quantity);

                if (consumable.getQuantity() < 0) {
                    AlertUtil.builder()
                            .alertType(Alert.AlertType.WARNING)
                            .header(STAGE_NAME)
                            .content("Итоговое количество не может быть меньше 0!")
                            .build().realise();
                    return;
                }
            }
        }

        request = Request.builder()
                .type(RequestType.UPDATE_CONSUMABLE)
                .data(gson.toJson(consumable))
                .build();

        response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        if (response == null) {
            return;
        }

        if (response.getStatus() == ResponseStatus.OK) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();

            loadTable();

            editConsumableId.setText("");
            editConsumableName.setText("");
            editConsumableQuantity.setText("");
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();
        }
    }

    @FXML
    void onDelButton(ActionEvent event) {
        if (editConsumableId.getText().isEmpty()) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Для удаления выберите номер!")
                    .build().realise();
            return;
        }

        long id = 0L;
        try {
            id = Long.parseLong(editConsumableId.getText());
        } catch (NumberFormatException e) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Формат номера не верен!")
                    .build().realise();
        }

        ButtonType confirmation = AlertUtil.builder()
                .alertType(Alert.AlertType.CONFIRMATION)
                .header(STAGE_NAME)
                .content("Вы уверены в удалении?")
                .build().realiseWithConfirmation();

        if (confirmation != ButtonType.OK) {
            return;
        }

        Consumable consumable = Consumable.builder()
                .id(id)
                .build();

        final Gson gson = new Gson();

        Request request = Request.builder()
                .type(RequestType.DELETE_CONSUMABLE)
                .data(gson.toJson(consumable))
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        if (response == null) {
            return;
        }

        if (response.getStatus() == ResponseStatus.OK) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();

            editConsumableId.setText("");
            editConsumableName.setText("");
            editConsumableQuantity.setText("");

            loadTable();
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header(STAGE_NAME)
                    .content(response.getMessage())
                    .build().realise();
        }
    }

    @Override
    public void setup() {
        consumableIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        consumableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        consumableQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        List<String> quantityOperations = new ArrayList<>();
        quantityOperations.add("Добавление");
        quantityOperations.add("Вычитание");
        quantityOperation.setItems(FXCollections.observableArrayList(quantityOperations));

        loadTable();
    }

    public void loadTable() {
        final Gson gson = new Gson();

        Request request = Request.builder()
                .type(RequestType.GET_ALL_CONSUMABLES)
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                "Настройка расходников"
        );

        if (response == null) {
            return;
        }

        if (response.getStatus() == ResponseStatus.OK) {
            Type listType = new TypeToken<List<Consumable>>() {}.getType();
            List<Consumable> consumables = gson.fromJson(response.getData(), listType);

            consumableTable.setItems(FXCollections.observableArrayList(consumables));
        } else {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header("Настройка расходников")
                    .content(response.getMessage())
                    .build().realise();
        }
    }
}

