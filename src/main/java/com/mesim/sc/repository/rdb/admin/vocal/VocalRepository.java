package com.mesim.sc.repository.rdb.admin.vocal;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocalRepository extends AdminRepository<Vocal, String> {

    Page<Vocal> findAll(Specification<Object> specification, Pageable pageable);

}
