package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.Master;
import by.bsuir.server.services.DBConnection;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Master> criteria = builder.createQuery(Master.class);
            Root<Master> root = criteria.from(Master.class);
            criteria.select(root);

            return session.createQuery(criteria).getResultList();
        }
    }
}
