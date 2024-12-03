package db.services;

import java.util.List;

public interface DBService<T> {
    T getById(Long id);
    void save(T entity);
    void delete(T entity);
    void update(T entity);
    List<T> getAll();
}
