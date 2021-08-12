package com.mesim.sc.api.rest.admin.Inspection;

import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.service.CrudService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/admin/inspectioninfo")
public class InspectionInfoRestController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("inspectionInfoService")
    public void setService(CrudService service) {
        this.name = "검수항목 정보";
        this.service = service;
    }

}
