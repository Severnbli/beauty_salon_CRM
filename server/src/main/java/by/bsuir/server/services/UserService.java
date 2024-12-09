package by.bsuir.server.services;

import by.bsuir.server.db.dao.*;
import by.bsuir.server.db.entities.*;
import by.bsuir.server.utils.EmailSender;
import by.bsuir.server.utils.Randomizer;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import by.bsuir.tcp.ResponseStatus;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.server.utils.Nullifable;
import com.google.gson.GsonBuilder;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalTime;

public class UserService implements Nullifable {
    private PersonDataDAO personDataDao = new PersonDataDAO();
    private UserDAO userDao = new UserDAO();
    private MasterDAO masterDao = new MasterDAO();
    private MasterScheduleDAO masterScheduleDAO = new MasterScheduleDAO();
    private Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

    public Response login(Request req) {
        final User userFromRequest = gson.fromJson(req.getData(), User.class);

        if (userFromRequest == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о пользователе не разобраны!")
                    .build();
        }

        User user = userDao.getUserWithSuchLogin(userFromRequest.getLogin());

        if (user != null && BCrypt.checkpw(userFromRequest.getPassword(), user.getPassword())) {
            return Response.builder()
                    .status(ResponseStatus.OK)
                    .message("Успешный вход!")
                    .data(gson.toJson(user))
                    .build();
        } else {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Пользователь с указанными данными не найден!")
                    .build();
        }
    }

    public Response register(Request req) {
        final User userFromRequest = gson.fromJson(req.getData(), User.class);

        if (userFromRequest == null || userFromRequest.getRole() == null || userFromRequest.getPersonData() == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о пользователе не разобраны!")
                    .build();
        }

        if (userDao.getUserWithSuchLogin(userFromRequest.getLogin()) != null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Пользователь с введённым логином существует!")
                    .build();
        }

        userFromRequest.setPassword(BCrypt.hashpw(userFromRequest.getPassword(), BCrypt.gensalt()));

        personDataDao.save(userFromRequest.getPersonData());
        userDao.save(userFromRequest);

        if (userDao.getUserWithSuchLogin(userFromRequest.getLogin()) != null) {
            return Response.builder()
                    .status(ResponseStatus.OK)
                    .message("Регистрация успешна!")
                    .build();
        } else {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Регистрация не удалась!")
                    .build();
        }
    }

    public Response updateProfile(Request req) {
        final User userFromRequest = gson.fromJson(req.getData(), User.class);

        if (userFromRequest == null || userFromRequest.getPersonData() == null || userFromRequest.getRole() == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о пользователе не разобраны!")
                    .build();
        }

        final User user = userDao.getById(userFromRequest.getId());

        if (user == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Пользователь не корректен!")
                    .build();
        }

        if (!user.getLogin().equals(userFromRequest.getLogin()) && userDao.getUserWithSuchLogin(userFromRequest.getLogin()) != null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Пользователь с подобным логином уже существует!")
                    .build();
        }

        if (!user.getPassword().equals(userFromRequest.getPassword())) {
            userFromRequest.setPassword(BCrypt.hashpw(userFromRequest.getPassword(), BCrypt.gensalt()));
        }
        
        userDao.update(userFromRequest);
        personDataDao.update(userFromRequest.getPersonData());

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Обновление данных успешно!")
                .data(gson.toJson(userFromRequest))
                .build();
    }

    public Response deleteProfile(Request req) {
        final User userFromRequest = gson.fromJson(req.getData(), User.class);

        if (userFromRequest == null || userFromRequest.getPersonData() == null || userFromRequest.getRole() == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о пользователе не разобраны!")
                    .build();
        }

        User user = userDao.getById(userFromRequest.getId());

        if (user == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Пользователь не корректен!")
                    .build();
        }

        if (user.getRole().getAccessLevel() >= 999) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Невозможно удаление аккаунта с ролью " + user.getRole().getName() + "!")
                    .build();
        }

        userDao.delete(userFromRequest);
        personDataDao.delete(userFromRequest.getPersonData());

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Аккаунт успешно удалён!")
                .build();
    }

    public Response getAllUsers() {
        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Пользователи успешно получены!")
                .data(gson.toJson(userDao.getAll()))
                .build();
    }

    public Response resetUserPasswordViaLogin(Request req) {
        final User userFromRequest = gson.fromJson(req.getData(), User.class);

        if (userFromRequest == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о пользователе не разобраны!")
                    .build();
        }

        User user = userDao.getUserWithSuchLogin(userFromRequest.getLogin());

        if (user == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Указанного пользователя не найдено в БД!")
                    .build();
        }

        String newUserPassword = new Randomizer().getRandomString(10);

        user.setPassword(BCrypt.hashpw(newUserPassword, BCrypt.gensalt()));
        userDao.update(user);

        new Thread(EmailSender.passwordEmail(
                user.getPersonData().getEmail(),
                "CRM - Салон красоты",
                newUserPassword
        )).start();

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Ваш новый пароль был отправлен вам на почту!")
                .build();
    }

    public Response isUserExistsByLogin(Request req) {
        final User userFromRequest = gson.fromJson(req.getData(), User.class);

        if (userFromRequest == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о пользователе не разобраны!")
                    .build();
        }

        User user = userDao.getUserWithSuchLogin(userFromRequest.getLogin());

        if (user == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Указанного пользователя не найдено в БД!")
                    .build();
        }

        return Response.builder()
                .status(ResponseStatus.OK)
                .data(gson.toJson(user))
                .build();
    }

    @Override
    public void nullify() {
        personDataDao = null;
        userDao = null;
        gson = null;
        masterDao = null;

        System.gc();
    }
}
