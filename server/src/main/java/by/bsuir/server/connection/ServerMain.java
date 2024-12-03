package by.bsuir.server.connection;

import by.bsuir.server.services.DBConnection;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerMain {
    private static final Logger log = Logger.getLogger(ServerMain.class);

    private static ServerSocket serverSocket;
    @Getter
    private static final List<Socket> clients = new ArrayList<>();

    public static void startServer() throws IOException {
        if (serverSocket != null) {
            log.warn("An attempt to restart the by.bsuir.server was detected!");
            return;
        }

        log.info("Starting by.bsuir.server...");

        createServerSocket();

        loadDatabase();

        while (true) {
            clients.removeIf(Socket::isClosed);

            log.info("Waiting for a client connection...");
            Socket clientSocket = serverSocket.accept();

            clients.add(clientSocket);

            log.info(
                    "Client IP: " + clientSocket.getInetAddress() + ", PORT: " + clientSocket.getLocalPort() +
                    " connected!"
            );

            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    private static void createServerSocket() throws IOException {
        log.info("Creating by.bsuir.server socket...");

        Dotenv dotenv = Dotenv.load();
        serverSocket = new ServerSocket(Integer.parseInt(dotenv.get("SERVER_PORT")));

        log.info("Server socket " + dotenv.get("SERVER_PORT") + " created successfully!");
    }

    public static void loadDatabase() {
        log.info("Loading database...");
        DBConnection.getSessionFactory();
    }
}
