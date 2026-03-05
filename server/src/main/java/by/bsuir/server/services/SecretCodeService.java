package by.bsuir.server.services;

import by.bsuir.server.db.dao.SecretCodeDAO;
import by.bsuir.server.db.dao.UserDAO;
import by.bsuir.server.db.entities.SecretCode;
import by.bsuir.server.db.entities.User;
import by.bsuir.server.utils.EmailSender;
import by.bsuir.server.utils.Nullifable;
import by.bsuir.server.utils.Randomizer;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.cdimascio.dotenv.Dotenv;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class SecretCodeService implements Nullifable {
    private Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();
    private SecretCodeDAO secretCodeDAO = new SecretCodeDAO();

    public Response makeSecretCode(Request req) {
        SecretCode secretCodeFromReq = gson.fromJson(req.getData(), SecretCode.class);

        if (secretCodeFromReq == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о секретном коде не разобраны!")
                    .build();
        }

        final String code = new Randomizer().getRandomString(6);

        new Thread(EmailSender.secretCodeEmail(
                secretCodeFromReq.getEmail(),
                "CRM - Салон красоты",
                code)).start();

        SecretCode secretCode = new SecretCode();
        secretCode.setEmail(secretCodeFromReq.getEmail());
        secretCode.setSecretCode(BCrypt.hashpw(code, BCrypt.gensalt()));
        secretCode.setTimestampOfFormation(LocalDateTime.now());

        int actionTimeInMinutes = Integer.parseInt(Dotenv.load().get("SECRET_CODE_ACTION_TIME_IN_MINUTES"));

        int hour = actionTimeInMinutes / 60;
        int minutes = actionTimeInMinutes % 60;

        secretCode.setActionTime(LocalTime.of(hour, minutes));

        secretCodeDAO.save(secretCode);

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Секретный код успешно отправлен на почту! Время действия: " + hour + " ч. " + minutes + " м.!")
                .build();
    }

    public Response isSecretCodeValid(Request req) {
        SecretCode secretCodeFromReq = gson.fromJson(req.getData(), SecretCode.class);

        if (secretCodeFromReq == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о секретном коде не разобраны!")
                    .build();
        }

        SecretCode secretCode = secretCodeDAO.getSecretCodeByEmail(secretCodeFromReq.getEmail());

        if (secretCode == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("На данный email ещё не приходило ни одного секретного кода!")
                    .build();
        }

        boolean isSecretCodeCompleteTheAction = LocalDateTime.now()
                .isAfter(secretCode.getTimestampOfFormation()
                        .plusHours(secretCode.getActionTime().getHour())
                        .plusMinutes(secretCode.getActionTime().getMinute())
                        .plusSeconds(secretCode.getActionTime().getSecond()));

        if (isSecretCodeCompleteTheAction) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Последний сформированный код исчерпал своё время!")
                    .build();
        }

        boolean isCodesMath = BCrypt.checkpw(secretCodeFromReq.getSecretCode(), secretCode.getSecretCode());

        if (!isCodesMath) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Секретный код неверен!")
                    .build();
        }

        return Response.builder()
                .status(ResponseStatus.OK)
                .data(gson.toJson(secretCode))
                .build();
    }

    @Override
    public void nullify() {
        gson = null;
        secretCodeDAO = null;

        System.gc();
    }
}
