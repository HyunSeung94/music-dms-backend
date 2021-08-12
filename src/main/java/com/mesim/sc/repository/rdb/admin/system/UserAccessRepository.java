package com.mesim.sc.repository.rdb.admin.system;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("userAccessRepository")
public interface UserAccessRepository extends AdminRepository<UserAccess, Integer> {

}
