package by.bsuir.client.controllers;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.Order;
import by.bsuir.client.models.Service;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.RequestType;
import by.bsuir.tcp.Response;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GraphController implements Initializable {
    private static final String STAGE_NAME = "График";
    private static final Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

    @Getter
    @Setter
    private static Stage stage;

    @FXML
    private BarChart<String, Number> graph;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBarChartData(getMockData());
    }

    private void loadBarChartData(Map<String, Long> data) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Map.Entry<String, Long> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        graph.getData().clear();
        graph.getData().add(series);
    }

    private Map<String, Long> getMockData() {
        Request request = Request.builder()
                .type(RequestType.GET_ALL_ORDERS)
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                STAGE_NAME
        );

        if (response != null) {
            Type listType = new TypeToken<List<Order>>() {}.getType();
            final List<Order> orders = gson.fromJson(response.getData(), listType);

            return orders.stream()
                    .collect(Collectors.groupingBy(
                            order -> order.getService().toString(),
                            Collectors.counting()
                    ));
        }

        return Map.of();
    }
}
