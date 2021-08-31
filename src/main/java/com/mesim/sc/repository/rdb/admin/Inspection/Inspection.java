package com.mesim.sc.repository.rdb.admin.Inspection;

import com.mesim.sc.repository.rdb.CrudEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_INSPECTION")
public class Inspection extends CrudEntity {

    @Id
    @Column(name = "INSPECTION_SQ")
    @SequenceGenerator(name = "COL_GEN_INSPECTION_ID_SEQ", sequenceName = "INSPECTION_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "COL_GEN_INSPECTION_ID_SEQ")
    private int id;

    @Column(name = "INSPECTION_ID")
    private String inspectionId;

    @Column(name = "SONG_CD")
    private String songCd;

    @Column(name = "VOCAL_CONTENTS_CD")
    private String vocalContentsCd;

    @Column(name = "ARRANGE_CONTENTS_CD")
    private String arrangeContentsCd;

    @Column(name = "RESULT_INS")
    private String resultIns;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name="INSPECTION_ID", referencedColumnName="INSPECTION_ID", insertable = false, updatable = false)
    private InspectionInfo inspectionInfo;

    @Builder
    public Inspection(int id, InspectionInfo inspectionInfo, String inspectionId, String songCd, String vocalContentsCd, String arrangeContentsCd, String resultIns, String regId, String modId) {
        super(regId, modId);
        this.id = id;
        this.inspectionInfo = inspectionInfo;
        this.inspectionId = inspectionId;
        this.songCd = songCd;
        this.vocalContentsCd = vocalContentsCd;
        this.arrangeContentsCd = arrangeContentsCd;
        this.resultIns = resultIns;
    }

}
