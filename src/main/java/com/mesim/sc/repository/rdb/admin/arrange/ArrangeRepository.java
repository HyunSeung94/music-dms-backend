package com.mesim.sc.repository.rdb.admin.arrange;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArrangeRepository extends AdminRepository<Arrange, String> {

    Page<Arrange> findAll(Specification<Object> specification, Pageable pageable);
    List<Arrange> findAllByStatus(String status);

}
