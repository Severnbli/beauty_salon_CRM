package by.bsuir.server.db.dao;

import java.util.List;

public interface DAO<T> {
    T getById(Long id);
    void save(T entity);
    void delete(T entity);
    void update(T entity);
    List<T> getAll();
    Long count();
}
