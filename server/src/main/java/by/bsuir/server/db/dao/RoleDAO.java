package by.bsuir.server.db.dao;

import by.bsuir.server.db.entities.Role;
import by.bsuir.server.services.DBConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class RoleDAO implements DAO<Role> {
    @Override
    public Role getById(Long id) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.get(Role.class, id);
        }
    }

    @Override
    public void save(Role entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(Role entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public void update(Role entity) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public List<Role> getAll() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("from Role", Role.class).getResultList();
        }
    }

    @Override
    public Long count() {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            return session.createQuery("select count(*) from Role", Long.class).getSingleResult();
        }
    }

    public Role getByAccessLevel(int accessLevel) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            final String hql = "from Role where accessLevel = :accessLevel";
            return session.createQuery(hql, Role.class).setParameter("accessLevel", accessLevel).uniqueResult();
        }
    }
}
