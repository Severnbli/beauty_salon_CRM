package by.bsuir.client.connection;

import by.bsuir.client.exceptions.ConnectionException;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
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
    private static final ServerClient instance = new ServerClient();
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    ServerClient() {
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
}
