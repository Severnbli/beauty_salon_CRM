package by.bsuir.client.utils;

import by.bsuir.client.connection.ServerClient;
import by.bsuir.client.models.Master;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.RequestType;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.google.gson.Gson;
import javafx.scene.control.Alert;

public class MasterUtils {
    public static Master loadMaster (String stageName) {
        Request request = Request.builder()
                .type(RequestType.GET_MASTER_BY_ID)
                .data(ServerClient.getInstance().getUser().getId().toString())
                .build();

        Response response = ServerClient.getInstance().makeRequestAndGetResponse(
                request,
                stageName
        );

        if (response == null) {
            return null;
        }

        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header(stageName)
                    .content("Не удалось подгрузить данные об мастере!")
                    .build().realise();
            return null;
        }

        return new Gson().fromJson(response.getData(), Master.class);
    }
}
