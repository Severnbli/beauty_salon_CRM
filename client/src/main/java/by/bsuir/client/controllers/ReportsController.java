package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.*;
import by.bsuir.client.utils.AlertUtil;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.RequestType;
import by.bsuir.tcp.Response;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class ReportsController {
    private static final Logger log = Logger.getLogger(ReportsController.class.getName());

    @Getter
    @Setter
    private static Stage stage;

    private static final String STAGE_NAME = "Формирование отчётов";

    @FXML
    private DatePicker endOnPicker;

    @FXML
    private Button exelReportButton;

    @FXML
    private Button pdfReportButton;

    @FXML
    private DatePicker startWithPicker;

    private List<User> lastUploadedUsersForReport;
    private List<Consumable> lastUploadedConsumablesForReport;
    private List<Service> lastUploadedServicesForReport;
    private List<Order> lastUploadedOrdersForReport;
    private List<MasterSchedule> lastUploadedMasterSchedule;

    @FXML
    void onExelReportButton(ActionEvent event) {
        if (isNotValidPickers()) {
            return;
        }

        loadData();

        File file = chooseFile("Сохранить Excel отчет", "Excel файлы", "*.xlsx");
        if (file == null) return;

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(file)) {

            createUsersSheet(workbook);
            createServicesSheet(workbook);
            createOrdersSheet(workbook);
            createConsumablesSheet(workbook);
            createMasterScheduleSheet(workbook);

            workbook.write(fos);

            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header(STAGE_NAME)
                    .content("Excel-отчёт успешно сгенерирован!")
                    .build().realise();
        } catch (Exception e) {
            log.severe("Exception on generating excel report: " + e);
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header(STAGE_NAME)
                    .content(e.toString())
                    .build().realise();
        }
    }

    @FXML
    void onPdfReportButton(ActionEvent event) {
        if (isNotValidPickers()) {
            return;
        }

        loadData();

        File file = chooseFile("Сохранить PDF отчет", "PDF файлы", "*.pdf");
        if (file == null) return;

        try (PdfWriter writer = new PdfWriter(file);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            PdfFont font = PdfFontFactory.createFont(Objects.requireNonNull(getClass().getResource("/fonts/times.ttf")).getPath(), PdfEncodings.IDENTITY_H);

            document.setFont(font);

            addUsersSection(document);
            addServicesSection(document);
            addOrdersSection(document);
            addConsumablesSection(document);
            addMasterScheduleSection(document);

            AlertUtil.builder()
                    .alertType(Alert.AlertType.INFORMATION)
                    .header(STAGE_NAME)
                    .content("PDF-отчёт успешно сгенерирован!")
                    .build().realise();

        } catch (Exception e) {
            log.severe("Exception on generating PDF report: " + e);
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header(STAGE_NAME)
                    .content(e.toString())
                    .build().realise();
        }
    }

    private void loadData() {
        lastUploadedConsumablesForReport = new ArrayList<>();
        lastUploadedMasterSchedule = new ArrayList<>();
        lastUploadedOrdersForReport = new ArrayList<>();
        lastUploadedUsersForReport = new ArrayList<>();
        lastUploadedServicesForReport = new ArrayList<>();
        int quantityOfRequests = 0;
        int quantityOfSuccessfulRequests = 0;

        Request request = Request.builder()
                .type(RequestType.GET_ALL_CONSUMABLES)
                .build();
        quantityOfRequests++;

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

        if (response != null) {
            quantityOfSuccessfulRequests++;

            Type listType = new TypeToken<List<Consumable>>() {}.getType();
            lastUploadedConsumablesForReport = gson.fromJson(response.getData(), listType);
        }

        request = Request.builder()
                .type(RequestType.GET_ALL_SERVICES)
                .build();
        quantityOfRequests++;

        response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        if (response != null) {
            quantityOfSuccessfulRequests++;

            Type listType = new TypeToken<List<Service>>() {}.getType();
            lastUploadedServicesForReport = gson.fromJson(response.getData(), listType);
        }

        request = Request.builder()
                .type(RequestType.GET_ALL_ORDERS)
                .build();
        quantityOfRequests++;

        response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        if (response != null) {
            quantityOfSuccessfulRequests++;

            Type listType = new TypeToken<List<Order>>() {}.getType();
            lastUploadedOrdersForReport = gson.fromJson(response.getData(), listType);

            if (startWithPicker.getValue() != null && endOnPicker.getValue() != null) {
                LocalDate startDate = startWithPicker.getValue();
                LocalDate endDate = endOnPicker.getValue();

                lastUploadedOrdersForReport.removeIf(order ->
                        order.getDate().toLocalDate().isBefore(startDate) ||
                                order.getDate().toLocalDate().isAfter(endDate)
                );
            }
        }

        request = Request.builder()
                .type(RequestType.GET_ALL_USERS)
                .build();
        quantityOfRequests++;

        response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        if (response != null) {
            quantityOfSuccessfulRequests++;

            Type listType = new TypeToken<List<User>>() {}.getType();
            lastUploadedUsersForReport = gson.fromJson(response.getData(), listType);
        }

        request = Request.builder()
                .type(RequestType.GET_ALL_MASTER_SCHEDULE)
                .build();
        quantityOfRequests++;

        response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        if (response != null) {
            quantityOfSuccessfulRequests++;

            Type listType = new TypeToken<List<MasterSchedule>>() {}.getType();
            lastUploadedMasterSchedule = gson.fromJson(response.getData(), listType);
        }

        AlertUtil.builder()
                .alertType(Alert.AlertType.INFORMATION)
                .header(STAGE_NAME)
                .content("Количество успешных запросов: " + quantityOfSuccessfulRequests + "/" + quantityOfRequests + ".")
                .build().realise();
    }

    private boolean isNotValidPickers() {
        if (startWithPicker.getValue() != null && endOnPicker != null &&
                startWithPicker.getValue().isAfter(endOnPicker.getValue())) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Промежуток даты не валиден. Перепроверьте!")
                    .build().realise();
            return true;
        }

        if (startWithPicker.getValue() == null || endOnPicker == null) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.WARNING)
                    .header(STAGE_NAME)
                    .content("Вы выбрали только начало / конец. Выберите вторую дату!")
                    .build().realise();
            return true;
        }

        return false;
    }

    private void addUsersSection(Document document) {
        document.add(new Paragraph("Пользователи").setBold());

        if (lastUploadedUsersForReport == null || lastUploadedUsersForReport.isEmpty()) {
            document.add(new Paragraph("Нет данных для отображения."));
            return;
        }

        Table table = new Table(new float[]{1, 2, 2, 2}).useAllAvailableWidth();
        table.addHeaderCell(new Cell().add(new Paragraph("ID")));
        table.addHeaderCell(new Cell().add(new Paragraph("Логин")));
        table.addHeaderCell(new Cell().add(new Paragraph("Имя")));
        table.addHeaderCell(new Cell().add(new Paragraph("Электронная почта")));

        for (User user : lastUploadedUsersForReport) {
            table.addCell(new Cell().add(new Paragraph(
                    user.getId() != null ? String.valueOf(user.getId()) : ""
            )));
            table.addCell(new Cell().add(new Paragraph(
                    user.getLogin() != null ? user.getLogin() : ""
            )));
            table.addCell(new Cell().add(new Paragraph(
                    user.getPersonData().getFirstName() != null ? user.getPersonData().getFirstName() : ""
            )));
            table.addCell(new Cell().add(new Paragraph(
                    user.getPersonData().getEmail() != null ? user.getPersonData().getEmail() : ""
            )));
        }

        document.add(table);
    }

    private void addServicesSection(Document document) {
        document.add(new Paragraph("Услуги").setBold());

        if (lastUploadedServicesForReport == null || lastUploadedServicesForReport.isEmpty()) {
            document.add(new Paragraph("Нет данных для отображения."));
            return;
        }

        Table table = new Table(new float[]{1, 2, 2, 2}).useAllAvailableWidth();
        table.addHeaderCell(new Cell().add(new Paragraph("ID")));
        table.addHeaderCell(new Cell().add(new Paragraph("Название")));
        table.addHeaderCell(new Cell().add(new Paragraph("Цена")));
        table.addHeaderCell(new Cell().add(new Paragraph("Длительность")));

        for (Service service : lastUploadedServicesForReport) {
            table.addCell(new Cell().add(new Paragraph(
                    service.getId() != null ? String.valueOf(service.getId()) : ""
            )));
            table.addCell(new Cell().add(new Paragraph(
                    service.getName() != null ? service.getName() : ""
            )));
            table.addCell(new Cell().add(new Paragraph(
                    service.getPrice() != null ? service.getPrice().toString() : ""
            )));
            table.addCell(new Cell().add(new Paragraph(
                    service.getTimeCost() != null ? service.getTimeCost().toString() : ""
            )));
        }

        document.add(table);
    }

    private void addOrdersSection(Document document) {
        document.add(new Paragraph("Заказы в период с " + startWithPicker.getValue() +
                " по " + endOnPicker.getValue()).setBold());

        if (lastUploadedOrdersForReport == null || lastUploadedOrdersForReport.isEmpty()) {
            document.add(new Paragraph("Нет данных для отображения."));
            return;
        }

        Table table = new Table(new float[]{1, 2, 2, 2, 2}).useAllAvailableWidth();
        table.addHeaderCell(new Cell().add(new Paragraph("ID")));
        table.addHeaderCell(new Cell().add(new Paragraph("Клиент")));
        table.addHeaderCell(new Cell().add(new Paragraph("Услуга")));
        table.addHeaderCell(new Cell().add(new Paragraph("Дата")));
        table.addHeaderCell(new Cell().add(new Paragraph("Мастер")));

        for (Order order : lastUploadedOrdersForReport) {
            table.addCell(new Cell().add(new Paragraph(
                    order.getId() != null ? String.valueOf(order.getId()) : ""
            )));
            table.addCell(new Cell().add(new Paragraph(
                    order.getClient() != null && order.getClient().getLogin() != null
                            ? order.getClient().getLogin()
                            : ""
            )));
            table.addCell(new Cell().add(new Paragraph(
                    order.getService() != null && order.getService().getName() != null
                            ? order.getService().getName()
                            : ""
            )));
            table.addCell(new Cell().add(new Paragraph(
                    order.getDate() != null
                            ? order.getDate().format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"))
                            : ""
            )));
            table.addCell(new Cell().add(new Paragraph(
                    order.getMaster() != null && order.getMaster().getUser() != null
                            ? order.getMaster().getUser().getPersonData().getFirstName()
                            : ""
            )));
        }

        document.add(table);
    }

    private void addConsumablesSection(Document document) {
        document.add(new Paragraph("Расходники").setBold());

        if (lastUploadedConsumablesForReport == null || lastUploadedConsumablesForReport.isEmpty()) {
            document.add(new Paragraph("Нет данных для отображения."));
            return;
        }

        Table table = new Table(new float[]{1, 2, 2}).useAllAvailableWidth();
        table.addHeaderCell(new Cell().add(new Paragraph("ID")));
        table.addHeaderCell(new Cell().add(new Paragraph("Название")));
        table.addHeaderCell(new Cell().add(new Paragraph("Количество")));

        for (Consumable consumable : lastUploadedConsumablesForReport) {
            table.addCell(new Cell().add(new Paragraph(
                    consumable.getId() != null ? String.valueOf(consumable.getId()) : ""
            )));
            table.addCell(new Cell().add(new Paragraph(
                    consumable.getName() != null ? consumable.getName() : ""
            )));
            table.addCell(new Cell().add(new Paragraph(
                    String.valueOf(consumable.getQuantity())
            )));
        }

        document.add(table);
    }

    private void addMasterScheduleSection(Document document) {
        document.add(new Paragraph("График мастеров").setBold());

        if (lastUploadedMasterSchedule == null || lastUploadedMasterSchedule.isEmpty()) {
            document.add(new Paragraph("Нет данных для отображения."));
            return;
        }

        Table table = new Table(new float[]{1, 2, 2, 2}).useAllAvailableWidth();
        table.addHeaderCell(new Cell().add(new Paragraph("ID")));
        table.addHeaderCell(new Cell().add(new Paragraph("Мастер")));
        table.addHeaderCell(new Cell().add(new Paragraph("День недели")));
        table.addHeaderCell(new Cell().add(new Paragraph("Время")));

        for (MasterSchedule schedule : lastUploadedMasterSchedule) {
            table.addCell(new Cell().add(new Paragraph(
                    schedule.getId() != null ? String.valueOf(schedule.getId()) : ""
            )));
            table.addCell(new Cell().add(new Paragraph(
                    schedule.getMaster() != null && schedule.getMaster().getUser() != null
                            ? schedule.getMaster().getUser().getLogin()
                            : ""
            )));
            table.addCell(new Cell().add(new Paragraph(
                    schedule.getDayOfWeek() != null ? schedule.getDayOfWeek().toString() : ""
            )));
            table.addCell(new Cell().add(new Paragraph(
                    schedule.getStartTime() != null && schedule.getEndTime() != null
                            ? schedule.getStartTime() + " - " + schedule.getEndTime()
                            : ""
            )));
        }

        document.add(table);
    }

    private void createUsersSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Пользователи");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Логин");
        headerRow.createCell(2).setCellValue("Имя");
        headerRow.createCell(3).setCellValue("Фамилия");
        headerRow.createCell(3).setCellValue("Электронная почта");

        int rowIndex = 1;
        for (User user : lastUploadedUsersForReport) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getLogin());
            row.createCell(2).setCellValue(user.getPersonData().getFirstName());
            row.createCell(3).setCellValue(user.getPersonData().getLastName());
            row.createCell(4).setCellValue(user.getPersonData().getEmail());
        }
    }

    private void createServicesSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Услуги");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Название");
        headerRow.createCell(2).setCellValue("Цена");
        headerRow.createCell(3).setCellValue("Длительность");

        int rowIndex = 1;
        for (Service service : lastUploadedServicesForReport) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(service.getId());
            row.createCell(1).setCellValue(service.getName());
            row.createCell(2).setCellValue(service.getPrice().doubleValue());
            row.createCell(3).setCellValue(service.getTimeCost().toString());
        }
    }

    private void createOrdersSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Заказы");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Клиент");
        headerRow.createCell(2).setCellValue("Услуга");
        headerRow.createCell(3).setCellValue("Дата");
        headerRow.createCell(4).setCellValue("Мастер");

        int rowIndex = 1;
        for (Order order : lastUploadedOrdersForReport) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(order.getId());
            row.createCell(1).setCellValue(order.getClient().getLogin());
            row.createCell(2).setCellValue(order.getService().getName());
            row.createCell(3).setCellValue(order.getDate().format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")));
            row.createCell(4).setCellValue(order.getMaster().getUser().getPersonData().getFirstName());
        }
    }

    private void createConsumablesSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Расходники");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Название");
        headerRow.createCell(2).setCellValue("Количество");

        int rowIndex = 1;
        for (Consumable consumable : lastUploadedConsumablesForReport) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(consumable.getId());
            row.createCell(1).setCellValue(consumable.getName());
            row.createCell(2).setCellValue(consumable.getQuantity());
        }
    }

    private void createMasterScheduleSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("График мастеров");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Мастер");
        headerRow.createCell(2).setCellValue("День недели");
        headerRow.createCell(3).setCellValue("Время");

        int rowIndex = 1;
        for (MasterSchedule schedule : lastUploadedMasterSchedule) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(schedule.getId());
            row.createCell(1).setCellValue(schedule.getMaster().getUser().getLogin());
            row.createCell(2).setCellValue(schedule.getDayOfWeek().toString());
            row.createCell(3).setCellValue(schedule.getStartTime() + " - " + schedule.getEndTime());
        }
    }

    private File chooseFile(String title, String description, String extension) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(description, extension));
        return fileChooser.showSaveDialog(new Stage());
    }
}
