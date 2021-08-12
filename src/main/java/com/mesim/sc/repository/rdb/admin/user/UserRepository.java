package com.mesim.sc.repository.rdb.admin.user;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("userRepository")
public interface UserRepository extends AdminRepository<User, String> {

}
