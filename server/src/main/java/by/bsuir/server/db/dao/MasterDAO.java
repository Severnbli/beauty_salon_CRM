package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.Master;
import by.bsuir.server.services.DBConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class MasterDAO implements DAO<Master> {
    @Override
    public Master getById(Long id) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.get(Master.class, id);
        }
    }

    @Override
    public void save(Master entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(Master entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public void update(Master entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public List<Master> getAll() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("from Master", Master.class).getResultList();
        }
    }

    @Override
    public Long count() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("select count(*) from Master", Long.class).getSingleResult();
        }
    }
}
