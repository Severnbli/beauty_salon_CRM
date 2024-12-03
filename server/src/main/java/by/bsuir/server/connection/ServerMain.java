package by.bsuir.server.connection;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static final Logger log = Logger.getLogger(ServerMain.class);

    private static ServerSocket serverSocket;

    public static void startServer() throws IOException {
        if (serverSocket != null) {
            log.warn("An attempt to restart the by.bsuir.server was detected!");
            return;
        }

        log.info("Starting by.bsuir.server...");

        createServerSocket();

        while (true) {
            log.info("Waiting for a client connection...");
            Socket clientSocket = serverSocket.accept();

            log.info(
                    "Client IP: " + clientSocket.getInetAddress() + ", PORT: " + clientSocket.getLocalPort() +
                    " connected!"
            );

            ClientHandler client;

            try {
                client = new ClientHandler(clientSocket);
                new Thread(client).start();
            } catch (IOException e) {
                log.error("An error while creating a new client handler: " + e);
            }
        }
    }

    private static void createServerSocket() throws IOException {
        log.info("Creating by.bsuir.server socket...");

        Dotenv dotenv = Dotenv.load();
        serverSocket = new ServerSocket(Integer.parseInt(dotenv.get("SERVER_PORT")));

        log.info("Server socket " + dotenv.get("SERVER_PORT") + " created successfully!");
    }
}
