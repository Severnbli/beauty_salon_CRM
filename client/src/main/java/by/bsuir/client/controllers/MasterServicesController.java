package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.Consumable;
import by.bsuir.client.models.Master;
import by.bsuir.client.models.MasterService;
import by.bsuir.client.models.Service;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.cert.ocsp.Req;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MasterServicesController implements Setupable {
    private static final String STAGE_NAME = "Навыки мастера";

    @Getter
    @Setter
    private static Stage stage;

    private Master master;

    @FXML
    private ListView<Service> masterServices;

    @FXML
    private ListView<Service> salonServices;

    @FXML
    void toMaster(ActionEvent event) {
        Service selectedService = salonServices.getSelectionModel().getSelectedItem();

        if (selectedService == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Выберите услугу, чтобы добавить в навыки!")
                    .build().realise();
            return;
        }

        MasterService masterService = MasterService.builder()
                .master(master)
                .service(selectedService)
                .build();

        final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        Request request = Request.builder()
                .type(RequestType.ADD_MASTER_SERVICE)
                .data(gson.toJson(masterService))
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

        loadServices();
    }

    @FXML
    void toSalon(ActionEvent event) {
        Service selectedService = masterServices.getSelectionModel().getSelectedItem();

        if (selectedService == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Выберите навык, чтобы от него отказаться!")
                    .build().realise();
            return;
        }


        MasterService masterService = MasterService.builder()
                .master(master)
                .service(selectedService)
                .build();

        final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        Request request = Request.builder()
                .type(RequestType.DELETE_MASTER_SERVICE)
                .data(gson.toJson(masterService))
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

        loadServices();
    }

    @Override
    public void setup() {
        loadMaster();
        loadServices();
    }

    private void loadServices() {
        Request request = Request.builder()
                .type(RequestType.GET_ALL_SERVICES)
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        List<Service> salonServicesFromDb = new ArrayList<>();

        if (response != null && response.getStatus() == ResponseStatus.OK) {
            Type listType = new TypeToken<List<Service>>() {}.getType();
            salonServicesFromDb = gson.fromJson(response.getData(), listType);
        }

        List<Service> masterServicesFromDb = new ArrayList<>();

        request = Request.builder()
                .type(RequestType.GET_SERVICES_THAT_MASTER_CAN_PERFORM)
                .data(gson.toJson(master))
                .build();

        response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        if (response != null && response.getStatus() == ResponseStatus.OK) {
            Type listType = new TypeToken<List<Service>>() {}.getType();
            masterServicesFromDb = gson.fromJson(response.getData(), listType);
        }

        masterServices.setItems(FXCollections.observableArrayList(masterServicesFromDb));

        salonServicesFromDb.removeAll(masterServicesFromDb);
        salonServices.setItems(FXCollections.observableArrayList(salonServicesFromDb));
    }

    private void loadMaster() {
        Request request = Request.builder()
                .type(RequestType.GET_MASTER_BY_ID)
                .data(ServerClient.getInstance().getUser().getId().toString())
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        if (response == null) {
            return;
        }

        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header(STAGE_NAME)
                    .content("Не удалось подгрузить данные об мастере!")
                    .build().realise();
            return;
        }

        master = new Gson().fromJson(response.getData(), Master.class);
    }
}