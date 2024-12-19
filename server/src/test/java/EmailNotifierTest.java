import by.bsuir.server.utils.EmailSender;
import org.junit.Test;

public class EmailNotifierTest {
    @Test
    public void isCanSendEmail() {
        EmailSender emailSender = new EmailSender(
                "usevaladbuben@gmail.com",
                "Проверка работоспособности системы оповещения...",
                ""
        );

        emailSender.sendEmail();
    }
}
