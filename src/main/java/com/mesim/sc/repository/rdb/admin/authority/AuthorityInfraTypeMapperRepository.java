package com.mesim.sc.repository.rdb.admin.authority;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("authorityInfraTypeMapperRepository")
public interface AuthorityInfraTypeMapperRepository extends AdminRepository<AuthorityInfraTypeMapper, AuthorityInfraTypeMapperKey> {

    Page<AuthorityInfraTypeMapper> findAllByRoleId(int roleId, Pageable pageable);

    List<AuthorityInfraTypeMapper> findAllByRoleId(int roleId);

    AuthorityInfraTypeMapper findByRoleIdAndInfraTypeCd(int roleId, String InfraTypeCd);

    List<AuthorityInfraTypeMapper> findByRoleId(int roleId);
}
