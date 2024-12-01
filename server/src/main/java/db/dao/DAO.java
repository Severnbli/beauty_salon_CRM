package db.dao;

import java.util.List;

public interface DAO<T> {
    T findById(Long id);
    void save(T entity);
    void delete(T entity);
    void update(T entity);
    List<T> getAll();
}
