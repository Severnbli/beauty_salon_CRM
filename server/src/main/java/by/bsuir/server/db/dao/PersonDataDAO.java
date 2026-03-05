package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.PersonData;
import by.bsuir.server.services.DBConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class PersonDataDAO implements DAO<PersonData> {
    @Override
    public PersonData getById(Long id) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.get(PersonData.class, id);
        }
    }

    @Override
    public void save(PersonData entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(PersonData entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public void update(PersonData entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public List<PersonData> getAll() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("from PersonData", PersonData.class).getResultList();
        }
    }

    @Override
    public Long count() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("select count(*) from PersonData", Long.class).getSingleResult();
        }
    }
}
