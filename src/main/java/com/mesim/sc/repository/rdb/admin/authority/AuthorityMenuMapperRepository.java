package com.mesim.sc.repository.rdb.admin.authority;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("roleMenuMapperRepository")
public interface AuthorityMenuMapperRepository extends AdminRepository<AuthorityMenuMapper, AuthorityMenuMapperKey> {

    Page<AuthorityMenuMapper> findAllByRoleId(int roleId, Pageable pageable);

    List<AuthorityMenuMapper> findAllByRoleId(int roleId);

}
