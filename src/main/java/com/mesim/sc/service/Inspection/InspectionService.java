package com.mesim.sc.service.Inspection;

import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.AdminSpecs;
import com.mesim.sc.repository.rdb.admin.Inspection.Inspection;
import com.mesim.sc.repository.rdb.admin.Inspection.InspectionRepository;

import com.mesim.sc.repository.rdb.admin.group.UserRepository;
import com.mesim.sc.service.UpdateMapperDto;
import com.mesim.sc.service.admin.AdminService;
import com.mesim.sc.service.admin.group.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@Qualifier("inspectionService")
public class InspectionService extends AdminService {

    @Autowired
    @Qualifier("inspectionRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.searchFields = new String[]{"id", "inspection_id","result_ins"};
        super.init();
    }



    public Object getList(String id) {
        return ((InspectionRepository) this.repository).findAllById(id)
                .stream()
                .map(InspectionDto::new)
                .collect(Collectors.toList());
    }

    public Object saveAll(Object o) throws IOException, BackendException {
        List<Inspection> list = new ArrayList<>();
        list = (ArrayList) o;
        List<Inspection> inspectionList = new ArrayList<>();
        for (int i=0;  i<list.size(); i++) {
            InspectionDto inspectionDto = super.mapper.convertValue(list.get(i), InspectionDto.class);

            Inspection inspection = Inspection.builder()
                    .id(inspectionDto.getId())
                    .inspectionId(inspectionDto.getInspectionId())
                    .resultIns(inspectionDto.getResultIns())
                    .build();
            inspectionList.add(inspection);
        }

//        List<Object> objList = (List<Object>) o;
//        List<Object> inspectionList = objList.stream()
//                .map(ExceptionHandler.wrap(data -> this.toEntity(data)))
//                .collect(Collectors.toList());

       return  ((InspectionRepository) this.repository).saveAll(inspectionList);
    }

    public List<InspectionDto> getListById(String id) {
        List<InspectionDto> inspectionDtoList = new ArrayList<>();
        ((InspectionRepository) this.repository).findAllById(id).forEach(inspection -> {
            inspectionDtoList.add(new InspectionDto(inspection));
        });
        return inspectionDtoList;
    }

}
