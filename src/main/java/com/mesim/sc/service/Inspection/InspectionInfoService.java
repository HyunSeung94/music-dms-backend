package com.mesim.sc.service.Inspection;

import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.AdminSpecs;

import com.mesim.sc.repository.rdb.admin.Inspection.InspectionInfo;
import com.mesim.sc.repository.rdb.admin.Inspection.InspectionInfoRepository;
import com.mesim.sc.repository.rdb.admin.Inspection.InspectionRepository;
import com.mesim.sc.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
        this.searchFields = new String[]{"id", "question", "inspectionCd"};

        super.init();
    }

}
