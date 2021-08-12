package com.mesim.sc.api.rest.admin.code;

import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.service.CrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/admin/codetype")
public class CodeTypeRestController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("codeTypeService")
    public void setService(CrudService service) {
        this.name = "공통코드 분류";
        this.service = service;
    }

}
