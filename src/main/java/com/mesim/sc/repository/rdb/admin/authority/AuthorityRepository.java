package com.mesim.sc.repository.rdb.admin.authority;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("roleRepository")
public interface AuthorityRepository extends AdminRepository<Authority, Integer> {

    List<Authority> findAllByOrderByRegDate();

    List<Authority> findAllByOrderByName();

}
