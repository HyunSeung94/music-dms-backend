package com.mesim.sc.repository.rdb.admin;

import com.mesim.sc.repository.rdb.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AdminRepository<T, ID>  extends CrudRepository<T, ID> {

}
