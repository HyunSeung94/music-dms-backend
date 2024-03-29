package com.mesim.sc.service.admin.Inspection;

import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.Inspection.Inspection;
import com.mesim.sc.repository.rdb.admin.Inspection.InspectionRepository;
import com.mesim.sc.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

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
        this.searchFields = new String[]{"id", "inspectionId", "resultIns"};

        super.init();
    }

    public Object saveAll(Object o) {
        List<Inspection> list = (ArrayList) o;
        List<Inspection> inspectionList = new ArrayList<>();

        for (int i = 0;  i < list.size(); i++) {
            InspectionDto inspectionDto = super.mapper.convertValue(list.get(i), InspectionDto.class);

            Inspection inspection = Inspection.builder()
                    .id(inspectionDto.getId())
                    .inspectionId(inspectionDto.getInspectionId())
                    .songCd(inspectionDto.getSongCd())
                    .vocalContentsCd(inspectionDto.getVocalContentsCd())
                    .arrangeContentsCd(inspectionDto.getArrangeContentsCd())
                    .resultIns(inspectionDto.getResultIns())
                    .regId(inspectionDto.getRegId())
                    .modId(inspectionDto.getModId())
                    .build();

            inspectionList.add(inspection);
        }

        if (inspectionList.size() > 0) {
            Inspection temp = inspectionList.get(0);
            if (temp.getSongCd() != null) {
                ((InspectionRepository) this.repository).deleteBySongCd(temp.getSongCd());
            } else if (temp.getVocalContentsCd() != null) {
                ((InspectionRepository) this.repository).deleteByVocalContentsCd(temp.getVocalContentsCd());
            } else if (temp.getArrangeContentsCd() != null) {
                ((InspectionRepository) this.repository).deleteByArrangeContentsCd(temp.getArrangeContentsCd());
            }
        }

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
