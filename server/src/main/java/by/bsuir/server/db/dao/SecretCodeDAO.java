package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.SecretCode;
import by.bsuir.server.services.DBConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class SecretCodeDAO implements DAO<SecretCode> {
    @Override
    public SecretCode getById(Long id) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.get(SecretCode.class, id);
        }
    }

    @Override
    public void save(SecretCode entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(SecretCode entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public void update(SecretCode entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public List<SecretCode> getAll() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("from SecretCode", SecretCode.class).getResultList();
        }
    }

    @Override
    public Long count() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("select count(*) from SecretCode", Long.class).getSingleResult();
        }
    }

    public SecretCode getSecretCodeByEmail(String email) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from SecretCode where email = :email order by timestampOfFormation desc limit 1",
                            SecretCode.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }
}
