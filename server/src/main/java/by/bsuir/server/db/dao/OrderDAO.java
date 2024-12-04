package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.Order;
import by.bsuir.server.services.DBConnection;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
            Root<Order> root = criteria.from(Order.class);
            criteria.select(root);

            return session.createQuery(criteria).getResultList();
        }
    }
}
