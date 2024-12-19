package by.bsuir.server.utils;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.apache.log4j.Logger;

import java.util.Properties;

public class EmailSender implements Runnable {
    private static final Logger log = Logger.getLogger(EmailSender.class);

    private final String to;
    private final String subject;
    private final String body;

    public EmailSender(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public static EmailSender secretCodeEmail(String to, String subject, String code) {
        return new EmailSender(
                to,
                subject,
                "Ваш секретный код: " + code + ".\n\n" +
                        "Ни в коем случае не передавайте этот код третьим лицам!\n\n" +
                        "Всего самого наилучшего!"
        );
    }

    public static EmailSender passwordEmail(String to, String subject, String password) {
        return new EmailSender(
                to,
                subject,
                "Ваш пароль: " + password + ".\n\n" +
                        "Не забудьте поменять его, сделав более надёжным!\n\n" +
                        "Ни в коем случае не передавайте свой пароль третьим лицам!\n\n" +
                        "Всего самого наилучшего!"
        );
    }

    @Override
    public void run() {
        sendEmail();
    }

    public void sendEmail() {
        if (to == null || subject == null || body == null) {
            log.info("Trying to send email was aborted via blank values!");
            return;
        }

        Dotenv dotenv = Dotenv.load();

        String host = dotenv.get("EMAIL_HOST");
        String port = dotenv.get("EMAIL_PORT");
        String username = dotenv.get("EMAIL_ADDRESS");
        String password = dotenv.get("EMAIL_PASSWORD");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            log.info("Email sending completed!");
        } catch (MessagingException e) {
            log.error("Email was not send to " + to + "!");

            throw new EmailException(e.getMessage());
        }
    }
}
