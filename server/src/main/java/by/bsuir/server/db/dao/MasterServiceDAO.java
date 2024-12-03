package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.MasterService;
import by.bsuir.server.services.DBConnection;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class MasterServiceDAO implements DAO<MasterService> {
    @Override
    public MasterService getById(Long id) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.get(MasterService.class, id);
        }
    }

    @Override
    public void save(MasterService entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(MasterService entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public void update(MasterService entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public List<MasterService> getAll() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<MasterService> criteria = builder.createQuery(MasterService.class);
            Root<MasterService> root = criteria.from(MasterService.class);
            criteria.select(root);

            return session.createQuery(criteria).getResultList();
        }
    }
}
