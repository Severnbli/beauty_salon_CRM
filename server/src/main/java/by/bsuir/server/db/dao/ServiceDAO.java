package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.Service;
import by.bsuir.server.services.DBConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ServiceDAO implements DAO<Service> {
    @Override
    public Service getById(Long id) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.get(Service.class, id);
        }
    }

    @Override
    public void save(Service entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(Service entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public void update(Service entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public List<Service> getAll() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("from Service", Service.class).getResultList();
        }
    }

    @Override
    public Long count() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("select count(*) from Service", Long.class).getSingleResult();
        }
    }
}
