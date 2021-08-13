package com.mesim.sc.service.admin.Inspection;

import com.mesim.sc.repository.rdb.CrudRepository;

import com.mesim.sc.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@Qualifier("inspectionInfoService")
public class InspectionInfoService extends AdminService {

    @Autowired
    @Qualifier("inspectionInfoRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.selectField = "inspectionCd";
        this.searchFields = new String[]{"id", "question", "songCd", "contentsCd", "arrangeId"};

        super.init();
    }

}
