package com.mesim.sc.service.admin.Inspection;

import com.mesim.sc.repository.rdb.admin.Inspection.Inspection;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InspectionDto extends AdminDto {

    private int id;
    private String inspectionId;
    private String songCd;
    private String vocalContentsCd;
    private String arrangeContentsCd;
    private String resultIns;
    private String question;

    public InspectionDto() {}

    public InspectionDto(Inspection entity) {
        this.id = entity.getId();
        this.inspectionId = entity.getInspectionId();
        this.songCd = entity.getSongCd();
        this.vocalContentsCd = entity.getVocalContentsCd();
        this.arrangeContentsCd = entity.getArrangeContentsCd();
        this.resultIns = entity.getResultIns();
        this.question = entity.getInspectionInfo() != null ? entity.getInspectionInfo().getQuestion() : null;
        this.regId = entity.getRegId();
        this.regDate = DateUtil.toFormat(entity.getRegDate().getTime());
        this.modId = entity.getModId();
        this.modDate = DateUtil.toFormat(entity.getModDate().getTime());
    }

    @Override
    public Inspection toEntity() {
        return Inspection.builder()
                .id(this.id)
                .inspectionId(this.inspectionId)
                .songCd(this.songCd)
                .vocalContentsCd(this.vocalContentsCd)
                .arrangeContentsCd(this.arrangeContentsCd)
                .resultIns(this.resultIns)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }

}
