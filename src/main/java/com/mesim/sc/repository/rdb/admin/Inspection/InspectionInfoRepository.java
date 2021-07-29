package com.mesim.sc.repository.rdb.admin.Inspection;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionInfoRepository extends AdminRepository<InspectionInfo, String> {

    Page<InspectionInfo> findAll(Specification<Object> specification, Pageable pageable);
    List<InspectionInfo> findAllByInspectionCd(String cd);


}
