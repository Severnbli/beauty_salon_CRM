package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.MasterSchedule;
import by.bsuir.server.services.DBConnection;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class MasterScheduleDAO implements DAO<MasterSchedule> {
    @Override
    public MasterSchedule getById(Long id) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.get(MasterSchedule.class, id);
        }
    }

    @Override
    public void save(MasterSchedule entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(MasterSchedule entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public void update(MasterSchedule entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public List<MasterSchedule> getAll() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<MasterSchedule> criteria = builder.createQuery(MasterSchedule.class);
            Root<MasterSchedule> root = criteria.from(MasterSchedule.class);
            criteria.select(root);

            return session.createQuery(criteria).getResultList();
        }
    }
}
