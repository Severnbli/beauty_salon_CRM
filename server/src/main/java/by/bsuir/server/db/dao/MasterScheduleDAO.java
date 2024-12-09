package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.DayOfWeek;
import by.bsuir.server.db.entities.MasterSchedule;
import by.bsuir.server.services.DBConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalTime;
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
            return session.createQuery("from MasterSchedule", MasterSchedule.class).getResultList();
        }
    }

    @Override
    public Long count() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("select count(*) from MasterSchedule", Long.class).getSingleResult();
        }
    }

    public MasterSchedule getMasterScheduleTimeByDayOfWeek(Long masterId, DayOfWeek dayOfWeek) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            final String hql = "from MasterSchedule where master.id = :masterId and dayOfWeek = :dayOfWeek";
            return session.createQuery(hql, MasterSchedule.class)
                    .setParameter("masterId", masterId)
                    .setParameter("dayOfWeek", dayOfWeek)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }

    public List<MasterSchedule> getMasterSchedulesByMasterId(Long masterId) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("from MasterSchedule where master.id = :masterId", MasterSchedule.class)
                    .setParameter("masterId", masterId)
                    .getResultList();
        }
    }
}
