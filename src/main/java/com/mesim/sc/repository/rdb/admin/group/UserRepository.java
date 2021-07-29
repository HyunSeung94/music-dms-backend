package com.mesim.sc.repository.rdb.admin.group;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("userRepository")
public interface UserRepository extends AdminRepository<User, String> {

    User findByGroupId(int groupId);

    User findByAuthorityId(int authorityId);

    List<User> findAllByGroupId(int groupId);


}
