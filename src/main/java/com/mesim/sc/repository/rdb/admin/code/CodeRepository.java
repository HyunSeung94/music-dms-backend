package com.mesim.sc.repository.rdb.admin.code;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeRepository extends AdminRepository<Code, CodePk> {

    Page<Code> findAll(Specification<Object> specification, Pageable pageable);

    Page<Code> findAllByTypeCd(String typeCd, Pageable pageable);

    List<Code> findAllByTypeCd(String typeCd);

    List<Code> findAllByTypeCdAndUseYnOrderByName(String cd, int useYn);

    Code findByCdAndTypeCd(String cd, String typeCd);

}
