package com.mesim.sc.repository.rdb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface CrudRepository<T, ID>  extends PagingAndSortingRepository<T, ID>, JpaRepository<T,ID> {

    List<T> findAll(Specification<Object> specification);

    List<T> findAll(Specification<Object> specification, Sort sort);

    Page<T> findAll(Specification<Object> specification, Pageable pageable);

    Optional<T> findOne(Specification<Object> specification);

    default List<T> saveAllAndFlush(Iterable<T> iterable) {
        List<T> list = saveAll(iterable);
        flush();
        return list;
    }

}
