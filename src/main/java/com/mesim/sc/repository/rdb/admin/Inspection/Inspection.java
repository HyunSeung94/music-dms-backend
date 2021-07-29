package com.mesim.sc.repository.rdb.admin.Inspection;

import com.mesim.sc.repository.rdb.CrudEntity;
import com.mesim.sc.repository.rdb.admin.group.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@IdClass(InspectionPk.class)
@Entity(name = "TB_ADMIN_IT_INSPECTION")
public class Inspection extends CrudEntity {

    @Id
    @Column(name = "ID")
    private String id;

    @Id
    @Column(name = "INSPECTION_ID")
    private String inspectionId;

    @Column(name = "RESULT_INS")
    private String resultIns;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name="INSPECTION_ID", referencedColumnName="INSPECTION_ID", insertable = false, updatable = false)
    private InspectionInfo inspectionInfo;

    @Builder
    public Inspection(String id,InspectionInfo inspectionInfo, String inspectionId, String resultIns, String regId, String modId) {
        super(regId, modId);
        this.id = id;
        this.inspectionInfo = inspectionInfo;
        this.inspectionId = inspectionId;
        this.resultIns = resultIns;
    }

}
