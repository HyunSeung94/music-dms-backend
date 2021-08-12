package com.mesim.sc.service.admin.group;

import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@Qualifier("groupService")
public class GroupService extends AdminService {

    @Autowired
    @Qualifier("groupRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.joinedSortField = new String[]{"pgroup", "type"};
        this.searchFields = new String[]{"pgroupName", "name"};
        this.excludeColumn = new String[]{"pid", "pName"};
        this.root.put("id", CommonConstants.GROUP_ROOT_ID);

        this.addRefEntity("pgroup","name");

        super.init();
    }

}
