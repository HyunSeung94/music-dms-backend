package com.mesim.sc.api.rest.admin.group;

import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.service.CrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/group")
public class GroupRestController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("groupService")
    public void setService(CrudService service) {
        this.name = "조직";
        this.service = service;
    }

}
