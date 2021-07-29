package com.mesim.sc.service.admin.authority;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.authority.AuthorityMenuMapper;
import com.mesim.sc.repository.rdb.admin.authority.AuthorityMenuMapperRepository;
import com.mesim.sc.service.UpdateMapperDto;
import com.mesim.sc.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@Qualifier("roleMenuMapperService")
public class AuthorityMenuMapperService extends AdminService {

    @Autowired
    @Qualifier("authorityMenuMapperRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.searchFields = new String[]{"name"};
        this.joinedSortField = new String[]{"menu"};

        this.addRefEntity("menu", "pid");

        super.init();
    }

    @Override
    public List<AuthorityMenuMapperDto> get(String id) {
        int roleId = Integer.parseInt(id);
        List<AuthorityMenuMapper> list = ((AuthorityMenuMapperRepository) this.repository).findAllByRoleId(roleId);

        return list.stream()
            .map(AuthorityMenuMapperDto::new)
            .collect(Collectors.toList());
    }

    public PageWrapper getListPage(String id, int index, int size) {
        int roleId = Integer.parseInt(id);
        String[] sortProperties = { "menuPid;asc" };

        PageRequest pageRequest = super.getPageRequest(index, size, sortProperties);

        Page<AuthorityMenuMapper> page = ((AuthorityMenuMapperRepository) this.repository).findAllByRoleId(roleId, pageRequest);

        PageWrapper result = new PageWrapper(page);
        final AtomicInteger i = new AtomicInteger(1);

        result.setList(page.get()
            .map(ExceptionHandler.wrap(entity -> this.toDto(entity, i.getAndIncrement() + (result.getNumber() * size))))
            .collect(Collectors.toList())
        );

        return result;
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
