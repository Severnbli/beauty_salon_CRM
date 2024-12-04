package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.PersonData;
import by.bsuir.server.services.DBConnection;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class PersonDataDAO implements DAO<PersonData> {
    @Override
    public PersonData getById(Long id) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.get(PersonData.class, id);
        }
    }

    @Override
    public void save(PersonData entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(PersonData entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public void update(PersonData entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public List<PersonData> getAll() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PersonData> criteria = builder.createQuery(PersonData.class);
            Root<PersonData> root = criteria.from(PersonData.class);
            criteria.select(root);

            return session.createQuery(criteria).getResultList();
        }
    }
}
