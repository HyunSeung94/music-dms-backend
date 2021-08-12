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
@Qualifier("codeService")
public class CodeService extends AdminService {

    @Autowired
    @Qualifier("codeRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.selectField = "typeCd";
        this.joinedSortField = new String[]{"type"};
        this.searchFields = new String[]{"cd", "typeCd", "typeName", "name"};

        this.addRefEntity("type", "name");

        super.init();
    }

}
