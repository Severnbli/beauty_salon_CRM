package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.Master;
import by.bsuir.server.db.entities.MasterService;
import by.bsuir.server.services.DBConnection;
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
            return session.createQuery("from MasterService", MasterService.class).getResultList();
        }
    }

    @Override
    public Long count() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("select count(*) from MasterService", Long.class).getSingleResult();
        }
    }

    public List<Master> getMastersByServiceId(Long serviceId) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            final String hql = "SELECT ms.master FROM MasterService ms WHERE ms.service.id = :serviceId";
            return session.createQuery(hql, Master.class)
                    .setParameter("serviceId", serviceId)
                    .getResultList();
        }
    }
}
