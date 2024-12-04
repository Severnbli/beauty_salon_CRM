package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.ServiceConsumable;
import by.bsuir.server.services.DBConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ServiceConsumableDAO implements DAO<ServiceConsumable> {
    @Override
    public ServiceConsumable getById(Long id) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.get(ServiceConsumable.class, id);
        }
    }

    @Override
    public void save(ServiceConsumable entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(ServiceConsumable entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public void update(ServiceConsumable entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public List<ServiceConsumable> getAll() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("from ServiceConsumable", ServiceConsumable.class).getResultList();
        }
    }

    @Override
    public Long count() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("select count(*) from ServiceConsumable", Long.class).getSingleResult();
        }
    }
}
