package com.mesim.sc.api.rest.admin.consortium;

import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.service.CrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/admin/consortium")
public class ConsortiumController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("consortiumService")
    public void setService(CrudService service) {
        this.name = "컨소시엄별 리스트";
        this.service = service;
    }

}
