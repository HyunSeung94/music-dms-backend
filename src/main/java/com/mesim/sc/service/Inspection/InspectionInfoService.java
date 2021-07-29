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
        this.searchFields = new String[]{"id", "question","inspection_cd"};
        super.init();
    }


    public PageWrapper getListPage(String inspection_cd, int index, int size, String[] sortProperties, String[] keywords, String searchOp) throws BackendException {
        Specification<Object> spec = AdminSpecs.inspectionCd(inspection_cd);
        PageRequest pageRequest = super.getPageRequest(index, size, sortProperties);

        if (keywords != null && keywords.length > 0) {
            spec = Specification.where(getSearchSpec(keywords, this.searchFieldSet, searchOp)).and(spec);
        }

        Page<InspectionInfo> page = ((InspectionInfoRepository) this.repository).findAll(spec, pageRequest);

        PageWrapper result = new PageWrapper(page);
        final AtomicInteger i = new AtomicInteger(1);

        result.setList(page.get()
            .map(ExceptionHandler.wrap(entity -> this.toDto(entity, i.getAndIncrement() + (result.getNumber() * size))))
            .collect(Collectors.toList())
        );

        return result;
    }
    /**
     * 데이터 상세조회 (inspection_cd)
     *
     * @param cd ID
     * @return 데이터 Dto
     */
    public Object getList(String cd) {
        return ((InspectionInfoRepository) this.repository).findAllByInspectionCd(cd)
                .stream()
                .map(InspectionInfoDto::new)
                .collect(Collectors.toList());
    }


}
