package by.bsuir.server.connection;

import by.bsuir.server.services.*;
import by.bsuir.tcp.ResponseStatus;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import org.apache.log4j.Logger;
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
    private UserService userService;
    private RoleService roleService;
    private OrderService orderService;
    private ServiceService serviceService;
    private MasterServiceService masterServiceService;
    private MasterServices masterServices;
    private ConsumableService consumableService;
    private ServiceConsumableService serviceConsumableService;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    private void clientSetup() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        userService = new UserService();
        roleService = new RoleService();
        orderService = new OrderService();
        serviceService = new ServiceService();
        masterServiceService = new MasterServiceService();
        masterServices = new MasterServices();
        consumableService = new ConsumableService();
        serviceConsumableService = new ServiceConsumableService();
    }

    @Override
    public void run() {
        try {
            clientSetup();
        } catch (IOException e) {
            log.error("Attempt to create client object was failed. Session was aborted!");
            return;
        }

        while(socket.isConnected()) {
            try {
                operate();
            } catch (IOException e) {
                log.info("Client IP: " + socket.getInetAddress() + ", PORT: " + socket.getPort() + " disconnected.");
                ServerMain.getClients().remove(socket);
                break;
            } catch (Exception e) {
                log.error("Error while operating client: " + e);

                response = Response.builder()
                        .status(ResponseStatus.ERROR)
                        .message("Сервер закрыл соединение!")
                        .build();

                try {
                    sendResponse();
                } catch (IOException responseException) {
                    log.error("Error while writing bad response: " + responseException);
                } finally {
                    nullify();
                }
            }
        }
    }

    private void operate() throws IOException, ClassNotFoundException {
        request = (Request) in.readObject();

        if (request == null) {
            response = Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка получения запроса!")
                    .build();
        } else {
            switch (request.getType()) {
                case LOGIN: {
                    response = userService.login(request);
                    break;
                }
                case REGISTER: {
                    response = userService.register(request);
                    break;
                }
                case DELETE_PROFILE: {
                    response = userService.deleteProfile(request);
                    break;
                }
                case UPDATE_PROFILE: {
                    response = userService.updateProfile(request);
                    break;
                }
                case ROLE_BY_ACCESS_LEVEL: {
                    response = roleService.roleByAccessLevel(request);
                    break;
                }
                case REJECT_ORDER: {
                    response = orderService.rejectOrder(request);
                    break;
                }
                case GET_ORDERS_BY_USER_ID: {
                    response = orderService.getOrdersByClientId(request);
                    break;
                }
                case GET_ALL_SERVICES: {
                    response = serviceService.getAllServices();
                    break;
                }
                case GET_MASTERS_BY_SERVICE_AND_DATE: {
                    response = masterServiceService.getMastersByServiceAndDate(request);
                    break;
                }
                case ADD_ORDER: {
                    response = orderService.addOrder(request);
                    break;
                }
                case GET_MASTER_BY_ID: {
                    response = masterServices.getMasterById(request);
                    break;
                }
                case GET_ALL_CONSUMABLES: {
                    response = consumableService.getAllConsumables();
                    break;
                }
                case ADD_CONSUMABLE: {
                    response = consumableService.addConsumable(request);
                    break;
                }
                case GET_CONSUMABLE_BY_ID: {
                    response = consumableService.getConsumableById(request);
                    break;
                }
                case DELETE_CONSUMABLE: {
                    response = consumableService.deleteConsumable(request);
                    break;
                }
                case UPDATE_CONSUMABLE: {
                    response = consumableService.updateConsumable(request);
                    break;
                }
                case GET_CONSUMABLES_BY_SERVICE: {
                    response = serviceConsumableService.getConsumablesByService(request);
                    break;
                }
                case ADD_SERVICE: {
                    response = serviceService.addService(request);
                    break;
                }
                case DELETE_SERVICE: {
                    response = serviceService.delService(request);
                    break;
                }
                case UPDATE_SERVICE: {
                    response = serviceService.updateService(request);
                    break;
                }
                case ADD_SERVICE_CONSUMABLE: {
                    response = serviceConsumableService.addServiceConsumable(request);
                    break;
                }
                case DELETE_SERVICE_CONSUMABLE: {
                    response = serviceConsumableService.delServiceConsumable(request);
                    break;
                }
                default: {
                    response = Response.builder()
                            .status(ResponseStatus.ERROR)
                            .message("Неизвестный запрос!")
                            .build();
                    break;
                }
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
            out.close();
            in.close();
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
            roleService.nullify();
            orderService.nullify();
            serviceService.nullify();
            masterServiceService.nullify();
            masterServices.nullify();
            consumableService.nullify();
            serviceConsumableService.nullify();

            userService = null;
            roleService = null;
            orderService = null;
            serviceService = null;
            masterServiceService = null;
            masterServices = null;
            consumableService = null;
            serviceConsumableService = null;

            System.gc();
        }
    }
}
