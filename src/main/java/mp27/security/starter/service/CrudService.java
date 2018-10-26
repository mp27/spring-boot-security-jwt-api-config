package mp27.security.starter.service;


import java.util.List;
import java.util.Set;

public interface CrudService<T, ID> {
    List<T> findAll();

    T findById(ID id);

    List<T> findByIdIn(List<ID> ids);

    T save(T object);

    void delete(T object);

    void deleteById(ID id);
}
