package com.mesim.sc.repository.rdb.admin.consortium;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import com.mesim.sc.repository.rdb.admin.code.CodePk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsortiumRepository extends AdminRepository<Consortium, String> {

    Page<Consortium> findAll(Specification<Object> specification, Pageable pageable);

    List<Consortium> findAllByGroupIdAndRoleOrderById(String groupId,String role);

    List<Consortium> findAllByGroupIdOrderById(String groupId);


}
