package com.mesim.sc.repository.rdb.admin.group;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("groupRepository")
public interface GroupRepository extends AdminRepository<Group, Integer> {

    List<Group> findAllByIdNotOrderByName(int id);

}
