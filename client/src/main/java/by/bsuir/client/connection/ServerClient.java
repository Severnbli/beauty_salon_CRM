package by.bsuir.client.connection;

import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

@Getter
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
            socket = new Socket(dotenv.get("SERVER_IP"), Integer.parseInt(dotenv.get("SERVER_PORT")));
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            log.severe("Attempt of connection to server failed. Reason: " + e);
        }
    }

    public void sendRequest(Request request) throws IOException {
        out.writeObject(request);
        out.flush();
    }

    public Response getResponse() throws IOException, ClassNotFoundException {
        return (Response) in.readObject();
    }
}
