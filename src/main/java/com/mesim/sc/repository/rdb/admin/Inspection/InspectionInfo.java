package com.mesim.sc.repository.rdb.admin.Inspection;

import com.mesim.sc.repository.rdb.CrudEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_INSPECTIONINFO")
public class InspectionInfo extends CrudEntity {

    @Id
    @Column(name = "INSPECTION_ID")
    private String inspectionId;

    @Column(name = "QUESTION")
    private String question;

    @Column(name = "INSPECTION_CD")
    private String inspectionCd;

    @Builder
    public InspectionInfo(
            String inspectionId,
            String question,
            String inspectionCd,
            String regId,
            String modId
    ) {
        super(regId, modId);

        this.inspectionId = inspectionId;
        this.question = question;
        this.inspectionCd = inspectionCd;
    }

}
