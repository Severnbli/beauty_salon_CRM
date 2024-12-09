package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.Master;
import by.bsuir.client.models.MasterSchedule;
import by.bsuir.client.utils.AlertUtil;
import by.bsuir.client.utils.MasterUtils;
import by.bsuir.client.utils.Setupable;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.RequestType;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

public class MasterScheduleController implements Setupable {
    private static final String STAGE_NAME = "Настройка расписания";

    @Setter
    @Getter
    private static Stage stage;

    private Master master;

    @FXML
    private TableColumn<MasterSchedule, String> dayOfWeekColumn;

    @FXML
    private TableColumn<MasterSchedule, LocalTime> endTimeColumn;

    @FXML
    private TextArea infoArea;

    @FXML
    private TableView<MasterSchedule> scheduleTable;

    @FXML
    private TableColumn<MasterSchedule, LocalTime> startTimeColumn;

    private ObservableList<LocalTime> timeValues;

    @FXML
    void onSaveButton(ActionEvent event) {
        List<MasterSchedule> masterScheduleList = scheduleTable.getItems();

        final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        Request request = Request.builder()
                .type(RequestType.UPDATE_MASTER_SCHEDULES)
                .data(gson.toJson(masterScheduleList))
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

        AlertUtil.builder()
                .alertType(Alert.AlertType.INFORMATION)
                .header(STAGE_NAME)
                .content("Информация обновлена успешно!")
                .build().realise();

        loadTable();
    }

    @Override
    public void setup() {
        timeValues = FXCollections.observableList(
                IntStream.range(8, 21)
                        .mapToObj(hour -> List.of(LocalTime.of(hour, 0), LocalTime.of(hour, 30)))
                        .flatMap(List::stream)
                        .toList()
        );

        dayOfWeekColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDayOfWeek().getRussianName()));
        setupStartTimeColumn();
        setupEndTimeColumn();

        master = MasterUtils.loadMaster(STAGE_NAME);
        loadTable();
        calculateWorkingTimes();
    }

    private void loadTable() {
        if (master == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header(STAGE_NAME)
                    .content("Невозможно подгрузить расписание. Не известен мастер!")
                    .build().realise();
            return;
        }

        final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        Request request = Request.builder()
                .type(RequestType.GET_MASTER_SCHEDULES_BY_MASTER)
                .data(gson.toJson(master))
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

        Type listType = new TypeToken<List<MasterSchedule>>() {}.getType();
        List<MasterSchedule> masterSchedules = gson.fromJson(response.getData(), listType);

        scheduleTable.setItems(FXCollections.observableArrayList(masterSchedules));

        calculateWorkingTimes();
    }

    private void setupStartTimeColumn() {
        startTimeColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getStartTime()));
        startTimeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(timeValues));
        startTimeColumn.setOnEditCommit(event -> {
            MasterSchedule schedule = event.getRowValue();
            LocalTime newStartTime = event.getNewValue();

            if (newStartTime.plusMinutes(30).isAfter(schedule.getEndTime())) {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.WARNING)
                        .header(STAGE_NAME)
                        .content("Выбранное время не валидно!")
                        .build().realise();
                scheduleTable.refresh();
            } else {
                calculateWorkingTimes();
                schedule.setStartTime(newStartTime);
                scheduleTable.refresh();
            }
        });
    }

    private void setupEndTimeColumn() {
        endTimeColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getEndTime()));
        endTimeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(timeValues));
        endTimeColumn.setOnEditCommit(event -> {
            MasterSchedule schedule = event.getRowValue();
            LocalTime newEndTime = event.getNewValue();

            if (newEndTime.minusMinutes(30).isBefore(schedule.getStartTime())) {
                AlertUtil.builder()
                        .alertType(Alert.AlertType.WARNING)
                        .header(STAGE_NAME)
                        .content("Выбранное время не валидно!")
                        .build().realise();
                scheduleTable.refresh();
            } else {
                calculateWorkingTimes();
                schedule.setEndTime(newEndTime);
                scheduleTable.refresh();
            }
        });
    }

    public void calculateWorkingTimes () {
        List<MasterSchedule> masterSchedules = scheduleTable.getItems();

        Duration duration = Duration.ZERO;

        for (MasterSchedule masterSchedule : masterSchedules) {
            duration = duration.plus(Duration.between(masterSchedule.getStartTime(), masterSchedule.getEndTime()));
        }

        infoArea.setText("Ваша рабочая неделя составляет " + duration.toHours() + " часов!");
    }
}
