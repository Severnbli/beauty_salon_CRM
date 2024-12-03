package by.bsuir.server.db.services;

import by.bsuir.server.db.dao.DAO;
import by.bsuir.server.db.dao.UserDAO;
import by.bsuir.server.db.entities.User;

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
