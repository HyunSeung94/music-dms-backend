package com.mesim.sc.repository.rdb.board;

import com.mesim.sc.repository.rdb.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BoardRepository<T, ID>  extends CrudRepository<T, ID> {

}
