package com.mesim.sc.repository.rdb.admin.Inspection;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionRepository extends AdminRepository<Inspection, String> {

    Page<Inspection> findAll(Specification<Object> specification, Pageable pageable);

    List<Inspection> findAllById(String id);

    void deleteBySongCd(String songCd);

    void deleteByVocalContentsCd(String vocalContentsCd);

    void deleteByArrangeContentsCd(String arrangeContentsCd);

}
