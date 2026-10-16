package interfaces;

import java.util.List;

public interface IRepository<T> {

    boolean add(T entity);

    boolean update(T entity);

    boolean delete(int id);

    List<T> findAll();

    T findById(int id);

}