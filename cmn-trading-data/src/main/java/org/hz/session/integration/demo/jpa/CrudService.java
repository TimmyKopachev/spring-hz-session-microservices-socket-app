package org.hz.session.integration.demo.jpa;


import org.hz.session.integration.demo.model.BasicId;

import java.util.Collection;

public interface CrudService <T extends BasicId> {

    T save(T t);

    Iterable<T> saveAll(Collection<T> data);

    T update(T t);

    T get(Long id);

    void delete(Long id);

    Collection<T> findAll();

}
