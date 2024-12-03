package by.bsuir.server.connection;

import by.bsuir.tcp.ResponseStatus;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import org.apache.log4j.Logger;
import by.bsuir.server.services.UserService;
import by.bsuir.server.utils.Nullifable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable, Nullifable {
    private static final Logger log = Logger.getLogger(ClientHandler.class);
    private Socket socket;
    private Request request;
    private Response response;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    UserService userService;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    private void clientSetup() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        userService = new UserService();
    }

    @Override
    public void run() {
        try {
            clientSetup();

            while(socket.isConnected()) {
                operate();
            }
        } catch (Exception e) {
            ServerMain.getClients().remove(socket);

            log.error("Error while operating client: " + e);

            response = new Response(ResponseStatus.ERROR, "Сервер закрыл соединение!", "");

            try {
                sendResponse();
            } catch (IOException responseException) {
                log.error("Error while writing goodbye response: " + responseException);
            }
        } finally {
            nullify();
        }
    }

    private void operate() throws IOException, ClassNotFoundException {
        request = (Request) in.readObject();

        switch (request.getType()) {
            case LOGIN: {
                userService.login(request, response);
                break;
            }
            case REGISTER: {
                userService.register(request, response);
                break;
            }
            case UPDATE_PROFILE: {
                userService.update(request, response);
                break;
            }
            default: {
                response = new Response(ResponseStatus.ERROR, "Неизвестный запрос!", "");
                break;
            }
        }

        sendResponse();
    }

    private void sendResponse() throws IOException {
        out.writeObject(response);
        out.flush();
    }

    @Override
    public void nullify() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            log.error("Error while closing client streams: " + e);
        } finally {
            socket = null;
            request = null;
            response = null;

            in = null;
            out = null;

            userService.nullify();
            userService = null;

            System.gc();
        }
    }
}
