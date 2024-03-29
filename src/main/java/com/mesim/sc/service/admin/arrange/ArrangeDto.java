package com.mesim.sc.service.admin.arrange;

import com.mesim.sc.repository.rdb.admin.arrange.Arrange;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Date;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ArrangeDto extends AdminDto {

    private String id;
    private String arrangerCd;
    private String arrangerNm;
    private String arrangerRole;
    private String arrangeDate;
    private String ageRange;
    private String regNm;
    private String regGroupNm;
    private String importYn;
    private String status;
    private List fileList;

    public ArrangeDto() {}

    public ArrangeDto(Arrange entity) {
        this.id = entity.getId();
        this.arrangerCd = entity.getArrangerCd();
        if (entity.getArranger() != null) {
            this.arrangerNm = entity.getArranger().getConsortiumNm();
            this.arrangerRole = entity.getArranger().getRole();

        }
        if(entity.getVocal().getSinger() != null){
            this.ageRange = entity.getVocal().getSinger().getAgeRange();
        }
        this.arrangeDate = DateUtil.toFormat_yyyyMMdd(entity.getArrangeDate().getTime());
        this.importYn = entity.getImportYn();
        this.status = entity.getStatus();
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
                .arrangerCd(this.arrangerCd)
                .arrangeDate(this.arrangeDate.length() < 11 ? Date.valueOf(this.arrangeDate) : Date.valueOf(this.arrangeDate.substring(0,10)))
                .importYn(this.importYn != null ? this.importYn : "N")
                .status(this.status)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }

}
