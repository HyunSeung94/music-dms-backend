package com.mesim.sc.service.admin.song;

import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.AdminSpecs;
import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import com.mesim.sc.repository.rdb.admin.song.CreativeSongRepository;
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
@Qualifier("creativeSongService")
public class CreativeSongService extends AdminService {

    @Autowired
    @Qualifier("creativeSongRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.searchFields = new String[]{"id", "genre","composer","lyricist"};
        super.init();
    }


    public PageWrapper getListPage(String typeCd, int index, int size, String[] sortProperties, String[] keywords, String searchOp) throws BackendException {
        Specification<Object> spec = AdminSpecs.typeCd(typeCd);
        PageRequest pageRequest = super.getPageRequest(index, size, sortProperties);

        if (keywords != null && keywords.length > 0) {
            spec = Specification.where(getSearchSpec(keywords, this.searchFieldSet, searchOp)).and(spec);
        }

        Page<CreativeSong> page = ((CreativeSongRepository) this.repository).findAll(spec, pageRequest);

        PageWrapper result = new PageWrapper(page);
        final AtomicInteger i = new AtomicInteger(1);

        result.setList(page.get()
            .map(ExceptionHandler.wrap(entity -> this.toDto(entity, i.getAndIncrement() + (result.getNumber() * size))))
            .collect(Collectors.toList())
        );

        return result;
    }

    public Object getList(String typeCd) {
        return ((CreativeSongRepository) this.repository).findAllByGenre(typeCd)
                .stream()
                .map(CreativeSongDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 저장 (생성 또는 수정)
     *
     * @param o 데이터
     * @return 저장된 데이터
     */
    public Object save(Object o) throws BackendException {
        Object entity = this.toEntity(o);
        Object result = this.repository.saveAndFlush(entity);
        this.entityManager.refresh(result);
        return this.toDto(result, 0);
    }


}
