package db.services;

import db.dao.DAO;
import db.dao.UserDAO;
import db.entities.User;

import java.util.List;

public class UserService implements DBService<User> {
    DAO<User> dao = new UserDAO();

    @Override
    public User getById(Long id) {
        return dao.getById(id);
    }

    @Override
    public void save(User entity) {
        dao.save(entity);
    }

    @Override
    public void delete(User entity) {
        dao.delete(entity);
    }

    @Override
    public void update(User entity) {
        dao.update(entity);
    }

    @Override
    public List<User> getAll() {
        return dao.getAll();
    }
}
