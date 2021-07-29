package com.mesim.sc.service.admin.authority;

import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.authority.AuthorityInfraTypeMapper;
import com.mesim.sc.repository.rdb.admin.authority.AuthorityInfraTypeMapperRepository;
import com.mesim.sc.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@Qualifier("authorityInfraTypeMapperService")
public class AuthorityInfraTypeMapperService extends AdminService {

    @Autowired
    @Qualifier("authorityInfraTypeMapperRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.searchFields = new String[]{};
        super.init();
    }

    public PageWrapper listPageByRoleId(int id, int index, int size, String[] sortProperties, String[] keywords, String searchOp, String fromDate, String toDate) throws BackendException {
        Specification<Object> spec = null;
        PageRequest pageRequest = this.getPageRequest(index, size, sortProperties);

        if (fromDate != null && toDate != null) {
            spec = this.getDateSpec(fromDate, toDate);
        }

        if (keywords != null && keywords.length > 0) {
            spec = spec == null ?
                    Specification.where(getSearchSpec(keywords, this.searchFieldSet, searchOp)) :
                    Specification.where(getSearchSpec(keywords, this.searchFieldSet, searchOp)).and(spec);
        }

        Page<Object> page = spec == null ? this.repository.findAll(pageRequest) : this.repository.findAll(spec, pageRequest);

        PageWrapper result = new PageWrapper(page);
        final AtomicInteger i = new AtomicInteger(1);

        result.setList(page.get()
                .map(ExceptionHandler.wrap(entity -> this.toDto(entity, i.getAndIncrement() + (result.getNumber() * size))))
                .filter(f -> {
                    AuthorityInfraTypeMapperDto data = (AuthorityInfraTypeMapperDto) f;
                    return data.getRoleId() == id;
                })
                .collect(Collectors.toList())
        );

        return result;
    }

    public Object get(int roleId, String infraTypeCd) {
        AuthorityInfraTypeMapper entity = ((AuthorityInfraTypeMapperRepository) this.repository).findByRoleIdAndInfraTypeCd(roleId, infraTypeCd);
        return entity != null ? new AuthorityInfraTypeMapperDto(entity) : null;
    }

    public Object getByRoleId(int roleId) {
        return ((AuthorityInfraTypeMapperRepository) this.repository).findByRoleId(roleId)
                .stream()
                .map(AuthorityInfraTypeMapperDto::new)
                .collect(Collectors.toList());
    }
}
