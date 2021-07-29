package com.mesim.sc.repository.rdb.admin.code;

import com.mesim.sc.repository.rdb.CrudEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_CT_COMMONCODE")
@IdClass(CodePk.class)
@ToString
public class Code extends CrudEntity {

    @Id
    @Column(name = "CD")
    private String cd;

    @Column(name = "CD_NM")
    private String name;

    @Id
    @Column(name = "TYPE_CD")
    private String typeCd;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "TYPE_CD", referencedColumnName = "CD_TYPE", insertable = false, updatable = false)
    private CodeType type;

    @Column(name = "REF_VAL1")
    private String refVal1;

    @Column(name = "REF_VAL2")
    private String refVal2;

    @Column(name = "REF_VAL3")
    private String refVal3;

    @Column(name = "USE_YN")
    private int useYn;

    @Column(name = "RMK")
    protected String rmk;

    @Builder
    public Code(String cd, String typeCd, String name,
                String refVal1, String refVal2, String refVal3,
                int useYn,
                String rmk,
                String regId,
                String modId) {

        // 필수
        super(regId, modId);

        this.cd = cd;
        this.typeCd = typeCd;
        this.name = name;
        this.refVal1 = refVal1;
        this.refVal2 = refVal2;
        this.refVal3 = refVal3;
        this.useYn = useYn;
        this.rmk = rmk;
    }

}