package com.mesim.sc.repository.rdb.admin.system;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("userConnectRepository")
public interface UserConnectRepository extends AdminRepository<UserConnect, Integer> {

}
