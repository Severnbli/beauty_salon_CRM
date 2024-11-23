package server;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static ServerSocket serverSocket;

    public static void startServer() throws IOException {
        if (serverSocket != null) {
            return;
        }

        createServerSocket();

        while (true) {
            Socket clientSocket = serverSocket.accept();

            ClientHandler client = new ClientHandler(clientSocket);

            new Thread(client).start();
        }
    }

    private static void createServerSocket() throws IOException {
        Dotenv dotenv = Dotenv.load();
        serverSocket = new ServerSocket(Integer.parseInt(dotenv.get("SERVER_PORT")));
    }
}
