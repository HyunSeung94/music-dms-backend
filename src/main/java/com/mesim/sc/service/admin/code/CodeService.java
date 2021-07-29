package com.mesim.sc.service.admin.code;

import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.AdminSpecs;
import com.mesim.sc.repository.rdb.admin.code.Code;
import com.mesim.sc.repository.rdb.admin.code.CodeRepository;
import com.mesim.sc.service.admin.AdminService;
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
@Qualifier("codeService")
public class CodeService extends AdminService {

    @Autowired
    @Qualifier("codeRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.searchFields = new String[]{"cd", "name"};
        super.init();
    }

    @Override
    public Object save(Object o) throws BackendException {
        CodeDto codeDto = super.mapper.convertValue(o, CodeDto.class);
        Code code = ((CodeRepository) this.repository).findByCdAndTypeCd(codeDto.getCd(), codeDto.getTypeCd());

        if (codeDto.getRegDate() == null && code != null) {
            throw new BackendException(CommonConstants.EX_PK_VIOLATION);
        }

        return super.save(o);
    }

    public PageWrapper getListPage(String typeCd, int index, int size, String[] sortProperties, String[] keywords, String searchOp) throws BackendException {
        Specification<Object> spec = AdminSpecs.typeCd(typeCd);
        PageRequest pageRequest = super.getPageRequest(index, size, sortProperties);

        if (keywords != null && keywords.length > 0) {
            spec = Specification.where(getSearchSpec(keywords, this.searchFieldSet, searchOp)).and(spec);
        }

        Page<Code> page = ((CodeRepository) this.repository).findAll(spec, pageRequest);

        PageWrapper result = new PageWrapper(page);
        final AtomicInteger i = new AtomicInteger(1);

        result.setList(page.get()
            .map(ExceptionHandler.wrap(entity -> this.toDto(entity, i.getAndIncrement() + (result.getNumber() * size))))
            .collect(Collectors.toList())
        );

        return result;
    }

    public Object getList(String typeCd) {
        return ((CodeRepository) this.repository).findAllByTypeCd(typeCd)
                .stream()
                .map(CodeDto::new)
                .collect(Collectors.toList());
    }

    public Object getListSelect(String typeCd) {
        return ((CodeRepository) this.repository).findAllByTypeCdAndUseYnOrderByName(typeCd, 1)
                .stream()
                .map(CodeDto::new)
                .collect(Collectors.toList());
    }

    public Object getListMultiSelect(ArrayList typeCds) {
        HashMap<String, List<CodeDto>> map = new HashMap<String, List<CodeDto>>();

        typeCds.forEach(typeCd -> {
            List<Code> list = ((CodeRepository) this.repository).findAllByTypeCdAndUseYnOrderByName(typeCd.toString(),1);
            List<CodeDto> result = list.stream()
                .map(CodeDto::new)
                .collect(Collectors.toList());

            map.put(typeCd.toString(), result);
        });

        return map;
    }

    public Object get(String cd, String typeCd) {
        Code entity = ((CodeRepository) this.repository).findByCdAndTypeCd(cd, typeCd);
        return entity != null ? new CodeDto(entity) : null;
    }
}
