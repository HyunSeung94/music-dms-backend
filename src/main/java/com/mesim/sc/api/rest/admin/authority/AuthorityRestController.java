package com.mesim.sc.api.rest.admin.authority;

import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.service.CrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin/authority")
public class AuthorityRestController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("authorityService")
    public void setService(CrudService service) {
        this.name = "권한";
        this.service = service;
    }

}
