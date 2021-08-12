package com.mesim.sc.api.rest.admin.system;

import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.service.CrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin/userconnect")
public class UserConnectRestController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("userConnectService")
    public void setService(CrudService service) {
        this.name = "사용자 접속이력";
        this.service = service;
    }

}
