package com.mesim.sc.repository.rdb.admin.arrange;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArrangeRepository extends AdminRepository<Arrange, Integer> {

    Page<Arrange> findAll(Specification<Object> specification, Pageable pageable);

}
