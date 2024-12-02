package client.connection;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Getter
public class ServerClient {
    public static final ServerClient instance = new ServerClient();
    private static Socket socket;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;

    ServerClient() {
        final Dotenv dotenv = Dotenv.load();

        try {
            socket = new Socket(dotenv.get("SERVER_IP"), Integer.parseInt(dotenv.get("SERVER_PORT")));
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
