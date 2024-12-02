package server;

import enums.ResponseStatus;
import tcp.Request;
import tcp.Response;
import org.apache.log4j.Logger;
import db.utils.UserUtilities;
import utils.Nullifable;

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
    UserUtilities userUtilities;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
        userUtilities = new UserUtilities();
    }

    @Override
    public void run() {
        try {
            while(socket.isConnected()) {
                operate();
            }
        } catch (Exception e) {
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
                userUtilities.login(request, response);
                break;
            }
            case REGISTER: {
                userUtilities.register(request, response);
                break;
            }
            case UPDATE_PROFILE: {
                userUtilities.update(request, response);
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

            userUtilities.nullify();
            userUtilities = null;

            System.gc();
        }
    }
}
