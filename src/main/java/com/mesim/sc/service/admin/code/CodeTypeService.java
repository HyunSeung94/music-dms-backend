package com.mesim.sc.service.admin.code;

import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@Qualifier("codeTypeService")
public class CodeTypeService extends AdminService {

    @Autowired
    @Qualifier("codeTypeRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.searchFields = new String[]{"id", "name"};

        super.init();
    }

}
