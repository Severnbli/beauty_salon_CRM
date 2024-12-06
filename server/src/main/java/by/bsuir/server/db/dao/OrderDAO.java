package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.Order;
import by.bsuir.server.services.DBConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class OrderDAO implements DAO<Order> {
    @Override
    public Order getById(Long id) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.get(Order.class, id);
        }
    }

    @Override
    public void save(Order entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(Order entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public void update(Order entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public List<Order> getAll() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("from Order", Order.class).getResultList();
        }
    }

    @Override
    public Long count() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("select count(*) from Order", Long.class).getSingleResult();
        }
    }

    public List<Order> getOrdersByClientId(Long clientId) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            final String hql = "from Order where client_id = :clientId";
            return session.createQuery(hql, Order.class).setParameter("clientId", clientId).getResultList();
        }
    }
}
