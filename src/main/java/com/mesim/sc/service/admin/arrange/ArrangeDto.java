package com.mesim.sc.service.admin.arrange;

import com.mesim.sc.repository.rdb.admin.arrange.Arrange;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class ArrangeDto extends AdminDto {

    private int id;
    private String contentsCd;
    private String arrangerCd;
    private String arrangerNm;
    private String arrangerRole;
    private String arrangeDate;
    private String regNm;
    private String regGroupNm;

    private List fileList;

    public ArrangeDto() {}

    public ArrangeDto(Arrange entity) {
        this.id = entity.getId();
        this.contentsCd = entity.getContentsCd();
        this.arrangerCd = entity.getArrangerCd();
        if (entity.getArranger() != null) {
            this.arrangerNm = entity.getArranger().getConsortiumNm();
            this.arrangerRole = entity.getArranger().getRole();
        }
        this.arrangeDate = DateUtil.toFormat_yyyyMMdd(entity.getArrangeDate().getTime());
        this.regId = entity.getRegId();
        if (entity.getRegUser() != null) {
            this.regNm = entity.getRegUser().getName();
            this.regGroupNm = entity.getRegUser().getGroup() != null ? entity.getRegUser().getGroup().getName() : null;
        }
        this.regDate = DateUtil.toFormat(entity.getRegDate().getTime());
        this.modId = entity.getModId();
        this.modDate = DateUtil.toFormat(entity.getModDate().getTime());
    }

    @Override
    public Arrange toEntity() {
        return Arrange.builder()
                .id(this.id)
                .contentsCd(this.contentsCd)
                .arrangerCd(this.arrangerCd)
                .arrangeDate(this.arrangeDate.length() < 11 ? Date.valueOf(this.arrangeDate) : Date.valueOf(this.arrangeDate.substring(0,10)))
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }

}
