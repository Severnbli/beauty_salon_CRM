package by.bsuir.client.connection;

import by.bsuir.client.exceptions.ConnectionException;
import by.bsuir.client.models.User;
import by.bsuir.client.utils.AlertUtil;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.RequestType;
import by.bsuir.tcp.Response;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.scene.control.Alert;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

@Getter
@ToString
public class ServerClient {
    private static final Logger log = Logger.getLogger(ServerClient.class.getName());

    @Getter
    private static ServerClient instance = new ServerClient();
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    @Setter
    private User user;

    public void closeConnection() {
        log.info("Closing connection...");
        try {
            out.close();
            in.close();
            socket.close();
            log.info("Closing connection complete!");
        } catch (IOException e) {
            log.severe("Error while closing connection: " + e);
        }
    }

    public void makeConnection() {
        if (socket != null && !socket.isClosed()) {
            log.severe("Register attempt to start connection while connection is already open");
            return;
        }

        final Dotenv dotenv = Dotenv.load();

        log.info("Trying to connect to server with credentials: IP("+ dotenv.get("SERVER_IP")
                + ") & PORT("+ dotenv.get("SERVER_PORT")+ ")...");

        try {
            establishConnection(this, dotenv.get("SERVER_IP"), Integer.parseInt(dotenv.get("SERVER_PORT")));
            log.info("Successfully connected to server!");
        } catch (ConnectionException e) {
            log.severe("Failed to establish connection to server. Reason: " + e);
        }
    }

    private ServerClient() {}

    public void sendRequest(Request request) throws IOException {
        out.writeObject(request);
        out.flush();
    }

    public Response getResponse() throws IOException, ClassNotFoundException {
        return (Response) in.readObject();
    }

    private void establishConnection(ServerClient serverClient, String ip, int port) throws ConnectionException {
        try {
            serverClient.socket = new Socket(ip, port);
            serverClient.out = new ObjectOutputStream(socket.getOutputStream());
            serverClient.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    public Response makeRequestAndGetResponse(Request request, String headerForAlert) {
        Response response = null;

        try {
            sendRequest(request);
            response = getResponse();

            if (response == null) {
                throw new RuntimeException("не получен ответ от сервера");
            }
        } catch (Exception e) {
            log.severe("Attempt of login failed: " + e);
            AlertUtil.builder()
                    .alertType(Alert.AlertType.ERROR)
                    .header(headerForAlert)
                    .content("Ошибка: " + e + "!")
                    .build().realise();
        }

        return response;
    }
}
