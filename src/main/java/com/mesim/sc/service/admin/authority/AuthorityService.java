package com.mesim.sc.service.admin.authority;

import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.authority.AuthorityRepository;
import com.mesim.sc.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Qualifier("roleService")
public class AuthorityService extends AdminService {

    @Autowired
    @Qualifier("authorityRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.searchFields = new String[]{"name"};
        super.init();
    }

    @Override
    public List<Object> getList() {
        return new ArrayList<>(((AuthorityRepository) this.repository).findAllByOrderByRegDate());
    }

    public Object getListSelect() {
        return ((AuthorityRepository) this.repository).findAllByOrderByName();
    }
}
