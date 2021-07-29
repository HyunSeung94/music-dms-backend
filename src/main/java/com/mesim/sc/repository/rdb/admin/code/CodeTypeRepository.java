package com.mesim.sc.repository.rdb.admin.code;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeTypeRepository extends AdminRepository<CodeType, String> {

    List<CodeType> findAllByOrderByName();

}
