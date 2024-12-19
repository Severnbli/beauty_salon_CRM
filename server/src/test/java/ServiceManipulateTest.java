import by.bsuir.server.db.dao.ServiceDAO;
import by.bsuir.server.db.dao.UserDAO;
import by.bsuir.server.db.entities.Service;
import by.bsuir.server.db.entities.User;
import by.bsuir.server.services.ServiceService;
import by.bsuir.server.services.UserService;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.*;

public class ServiceManipulateTest {
    private Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

    @Test
    public void checkGetAllUsersFromService() {
        UserService userService = new UserService();

        Response response = userService.getAllUsers();

        if (response == null || response.getStatus() != ResponseStatus.OK || response.getData() == null) {
            fail();
        }

        Type listType = new TypeToken<List<User>>() {}.getType();
        List<User> usersFromService = gson.fromJson(response.getData(), listType);

        if (usersFromService == null) {
            fail();
        }
        Long countOfUsersFromService = (long) usersFromService.size();

        UserDAO userDAO = new UserDAO();

        if (userDAO == null) {
            fail();
        }

        Long countOfUsersFromDb = userDAO.count();

        assertEquals(countOfUsersFromService, countOfUsersFromDb);
    }

    @Test
    public void checkGetAllServicesFromService() {
        ServiceService service = new ServiceService();

        Response response = service.getAllServices();

        if (response == null || response.getStatus() != ResponseStatus.OK || response.getData() == null) {
            fail();
        }

        Type listType = new TypeToken<List<Service>>() {}.getType();
        List<Service> servicesFromService = gson.fromJson(response.getData(), listType);

        if (servicesFromService == null) {
            fail();
        }
        Long countOfServicesFromService = (long) servicesFromService.size();

        ServiceDAO serviceDAO = new ServiceDAO();

        if (serviceDAO == null) {
            fail();
        }

        Long countOfServicesFromDb = serviceDAO.count();

        assertEquals(countOfServicesFromService, countOfServicesFromDb);
    }
}
