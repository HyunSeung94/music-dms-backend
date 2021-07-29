package com.mesim.sc.repository.rdb.admin.authority;

import com.mesim.sc.repository.rdb.CrudEntity;
import com.mesim.sc.repository.rdb.admin.code.Code;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_ROLEINFRATYPEMAPPER")
@IdClass(AuthorityInfraTypeMapperKey.class)
@ToString
public class AuthorityInfraTypeMapper extends CrudEntity {

    @Id
    @Column(name = "ROLE_ID")
    private int roleId;

    @Id
    @Column(name = "INFRA_TYPE_CD")
    private String infraTypeCd;

    @Column(name = "AREA_CDS")
    private String  areaCds;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID", insertable = false, updatable = false)
    private Authority authority;

    @ManyToOne
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "INFRA_TYPE_CD", referencedColumnName = "CD", insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'FMUF002'", referencedColumnName = "TYPE_CD"))
    })
    private Code infraType;

    @Builder
    public AuthorityInfraTypeMapper(int roleId, String infraTypeCd, String areaCds,
                                    String regId, String modId) {

        this.roleId = roleId;
        this.infraTypeCd = infraTypeCd;
        this.areaCds = areaCds;
        this.regId = regId;
        this.modId = modId;
    }
}
