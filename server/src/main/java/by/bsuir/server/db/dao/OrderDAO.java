package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.Order;
import by.bsuir.server.services.DBConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
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
            final String hql = "from Order where client.id = :clientId";
            return session.createQuery(hql, Order.class).setParameter("clientId", clientId).getResultList();
        }
    }

    public List<Order> getOrdersByMasterAndDate(Long masterId, LocalDate date) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            final String hql = "from Order where master.id = :masterId and date between :date1 and :date2";
            return session.createQuery(hql, Order.class)
                    .setParameter("masterId", masterId)
                    .setParameter("date1", date.atStartOfDay())
                    .setParameter("date2", date.atTime(23, 59, 59))
                    .getResultList();
        }
    }

    public List<Order> getOrdersByMasterId(Long masterId) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            final String hql = "from Order where master.id = :masterId";
            return session.createQuery(hql, Order.class).setParameter("masterId", masterId).getResultList();
        }
    }
}
