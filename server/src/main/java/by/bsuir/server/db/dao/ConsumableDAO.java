package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.Consumable;
import by.bsuir.server.services.DBConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ConsumableDAO implements DAO<Consumable> {
    @Override
    public Consumable getById(Long id) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.get(Consumable.class, id);
        }
    }

    @Override
    public void save(Consumable entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(Consumable entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public void update(Consumable entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public List<Consumable> getAll() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("from Consumable", Consumable.class).getResultList();
        }
    }

    @Override
    public Long count() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("select count(*) from Consumable", Long.class).getSingleResult();
        }
    }

    public List<Consumable> getConsumablesByName(String name) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            final String hql = "from Consumable where name = :name";
            return session.createQuery(hql, Consumable.class).setParameter("name", name).getResultList();
        }
    }
}
