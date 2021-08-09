package com.mesim.sc.service.admin.consortium;

import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.AdminSpecs;
import com.mesim.sc.repository.rdb.admin.code.Code;
import com.mesim.sc.repository.rdb.admin.code.CodeRepository;
import com.mesim.sc.repository.rdb.admin.consortium.ConsortiumRepository;
import com.mesim.sc.service.admin.AdminService;
import com.mesim.sc.service.admin.code.CodeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@Qualifier("consortiumService")
public class ConsortiumService extends AdminService {

    @Autowired
    @Qualifier("consortiumRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        super.init();
    }

    public Object getListSelect(String groupId,String role) {
        return ((ConsortiumRepository) this.repository).findAllByGroupIdAndRoleOrderById(groupId, role)
                .stream()
                .map(ConsortiumDto::new)
                .collect(Collectors.toList());
    }

    public Object getList(String groupId) {
        return ((ConsortiumRepository) this.repository).findAllByGroupIdOrderById(groupId)
                .stream()
                .map(ConsortiumDto::new)
                .collect(Collectors.toList());
    }

}
