package com.mesim.sc.repository.rdb.admin.menu;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("menuRepository")
public interface MenuRepository extends AdminRepository<Menu, Integer> {

}
