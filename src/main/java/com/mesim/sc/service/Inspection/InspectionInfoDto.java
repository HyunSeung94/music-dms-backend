package com.mesim.sc.service.Inspection;

import com.mesim.sc.repository.rdb.admin.Inspection.InspectionInfo;
import com.mesim.sc.service.CrudDto;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;

@Data
public class InspectionInfoDto extends CrudDto {

    private String inspectionId;
    private String question;
    private String inspectionCd;


    public InspectionInfoDto() {
    }

    public InspectionInfoDto(InspectionInfo entity) {
        this.inspectionId = entity.getInspectionId();
        this.question = entity.getQuestion();
        this.inspectionCd = entity.getInspectionCd();
        this.regId = entity.getRegId();
        this.regDate = DateUtil.toFormat(entity.getRegDate().getTime());
        this.modId = entity.getModId();
        this.modDate = DateUtil.toFormat(entity.getModDate().getTime());

    }

    @Override
    public InspectionInfo toEntity() {
        return InspectionInfo.builder()
                .inspectionId(this.inspectionId)
                .question(this.question)
                .inspectionCd(this.inspectionCd)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }
}
