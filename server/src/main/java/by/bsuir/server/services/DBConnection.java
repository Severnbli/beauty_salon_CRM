package by.bsuir.server.services;

import by.bsuir.server.db.dao.PersonDataDAO;
import by.bsuir.server.db.dao.RoleDAO;
import by.bsuir.server.db.dao.UserDAO;
import by.bsuir.server.db.entities.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Comparator;

public class DBConnection {
    private static SessionFactory sessionFactory;
    private static final Logger log = Logger.getLogger(DBConnection.class);

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                sessionFactory = new Configuration()
                        .addAnnotatedClass(Consumable.class)
                        .addAnnotatedClass(Master.class)
                        .addAnnotatedClass(MasterService.class)
                        .addAnnotatedClass(Order.class)
                        .addAnnotatedClass(Role.class)
                        .addAnnotatedClass(MasterSchedule.class)
                        .addAnnotatedClass(Service.class)
                        .addAnnotatedClass(ServiceConsumable.class)
                        .addAnnotatedClass(User.class)
                        .addAnnotatedClass(PersonData.class)
                        .addAnnotatedClass(SecretCode.class)
                        .buildSessionFactory();
            } catch (Exception e) {
                log.error("Error while creating the session factory: ", e);
            }
        }
        return sessionFactory;
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            try {
                sessionFactory.close();
            } catch (Exception e) {
                log.error("Error while closing the session factory: ", e);
            } finally {
                sessionFactory = null;

                System.gc();
            }
        }
    }

    public static void makeFirstAdmin() {
        final UserDAO userDAO = new UserDAO();

        if (userDAO.count() == 0) {
            final Dotenv dotenv = Dotenv.load();

            final User user = new User();
            user.setLogin(dotenv.get("DEFAULT_ADMIN_LOGIN"));
            user.setPassword(BCrypt.hashpw(dotenv.get("DEFAULT_ADMIN_PASSWORD"), BCrypt.gensalt()));

            final RoleDAO roleDAO = new RoleDAO();

            final Role role = roleDAO.getAll().stream()
                    .max(Comparator.comparingInt(Role::getAccessLevel))
                    .orElse(null);

            if (role != null) {
                user.setRole(role);

                final PersonData personData = new PersonData();
                personData.setFirstName(dotenv.get("DEFAULT_ADMIN_LOGIN"));
                new PersonDataDAO().save(personData);

                user.setPersonData(personData);
                userDAO.save(user);
            }
        }
    }
}
