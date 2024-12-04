package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import by.bsuir.server.services.DBConnection;

import java.util.List;

public class UserDAO implements DAO<User> {
    @Override
    public User getById(Long id) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        }
    }

    @Override
    public void save(User entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(User entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public void update(User entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public List<User> getAll() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("from User", User.class).getResultList();
        }
    }

    @Override
    public Long count() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("select count(*) from User", Long.class).getSingleResult();
        }
    }

    public User getUserWithSuchLogin(String login) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.get(User.class, login);
        }
    }
}
