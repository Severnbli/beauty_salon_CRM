package by.bsuir.server.services;

import by.bsuir.db.entities.*;

import by.bsuir.server.db.entities.*;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

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
                        .addAnnotatedClass(Schedule.class)
                        .addAnnotatedClass(Service.class)
                        .addAnnotatedClass(ServiceConsumable.class)
                        .addAnnotatedClass(User.class)
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
}
