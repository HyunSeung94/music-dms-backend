package com.mesim.sc.repository.rdb.admin.authority;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("authorityMenuMapperRepository")
public interface AuthorityMenuMapperRepository extends AdminRepository<AuthorityMenuMapper, AuthorityMenuMapperPk> {

}
