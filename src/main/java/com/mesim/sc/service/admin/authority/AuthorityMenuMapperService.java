package com.mesim.sc.service.admin.authority;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.authority.AuthorityMenuMapper;
import com.mesim.sc.service.UpdateMapperDto;
import com.mesim.sc.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Qualifier("authorityMenuMapperService")
public class AuthorityMenuMapperService extends AdminService {

    @Autowired
    @Qualifier("authorityMenuMapperRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.selectField = "authorityId";
        this.joinedSortField = new String[]{"authority", "menu"};
        this.searchFields = new String[]{"authorityName", "menuName"};

        this.addRefEntity("authority", "name");
        this.addRefEntity("menu", "name");

        super.init();
    }

    @Transactional
    public Object update(Object o) throws BackendException, JsonProcessingException {
        String jsonStr = this.mapper.writeValueAsString(o);

        UpdateMapperDto updateMapperDto = mapper.readValue(jsonStr, UpdateMapperDto.class);

        // Insert
        List<AuthorityMenuMapper> list = new ArrayList<>();

        for (Object _o : updateMapperDto.getInsertList()) {
            AuthorityMenuMapper authorityMenuMapper = (AuthorityMenuMapper) this.toEntity(_o);
            list.add(authorityMenuMapper);
        }

        this.repository.saveAll(list);
        this.repository.flush();

        // Delete
        list.clear();

        for (Object _o : updateMapperDto.getDeleteList()) {
            AuthorityMenuMapper authorityMenuMapper = (AuthorityMenuMapper) this.toEntity(_o);
            list.add(authorityMenuMapper);
        }

        this.repository.deleteAll(list);

        return true;
    }

    @Transactional
    public Object updateDetail(Object o) throws BackendException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(o);

        UpdateMapperDto updateMapperDto = mapper.readValue(jsonStr, UpdateMapperDto.class);

        // Update
        List<AuthorityMenuMapper> list = new ArrayList<>();

        for (Object _o : updateMapperDto.getUpdateList()) {
            AuthorityMenuMapper authorityMenuMapper = (AuthorityMenuMapper) this.toEntity(_o);
            list.add(authorityMenuMapper);
        }

        this.repository.saveAll(list);
        this.repository.flush();

        return true;
    }

}
