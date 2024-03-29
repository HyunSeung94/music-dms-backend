package com.mesim.sc.repository.rdb.admin.consortium;

import com.mesim.sc.repository.rdb.admin.code.Code;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_CONSORTIUM")
public class Consortium {

    @Id
    @Column(name = "CONSORTIUM_ID")
    private String id;

    @Column(name = "GROUP_ID")
    private String groupId;

    @Column(name = "CONSORTIUM_NM")
    private String consortiumNm;

    @Column(name = "BALLADE")
    private Integer ballade;

    @Column(name = "DANCE")
    private Integer dance;

    @Column(name = "AGITATION")
    private Integer agitation;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "ARG_RANGE")
    protected String ageRange;

    @Column(name = "TONE_COLOR")
    protected String toneColor;

    @Column(name = "GENDER")
    protected String gender;

    @Column(name = "INITIAL")
    protected String initial;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "ARG_RANGE", referencedColumnName = "CD", insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'ARGRANGE'", referencedColumnName = "TYPE_CD"))
    })
    private Code codeArgRange;

    @Column(name = "LEVEL")
    protected String level;

    @Builder
    public Consortium(
            String id,
            String groupId,
            String consortiumNm,
            Integer ballade,
            Integer dance,
            Integer agitation,
            String role,
            String ageRange,
            String toneColor,
            String gender,
            String level,
            String initial
    ) {
        this.id = id;
        this.groupId = groupId;
        this.consortiumNm = consortiumNm;
        this.ballade = ballade;
        this.dance = dance;
        this.agitation = agitation;
        this.role = role;
        this.ageRange = ageRange;
        this.toneColor = toneColor;
        this.gender = gender;
        this.level = level;
        this.initial = initial;
    }

}
